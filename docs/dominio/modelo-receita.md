# Modelo de Dados — Receita

Schema, factories e ciclo de vida das entidades `Receita` e `ReceitaItem` (módulo receituário).

---

## 1. Tabela `hsg.tb_receita`

Migration: [`V33__create_receita.sql`](../../infra/db/migrations/V33__create_receita.sql)

| Coluna | Tipo | Nullable | Descrição |
|---|---|---|---|
| `id_receita`   | BIGINT     | NOT NULL PK | Sequence `seq_receita` |
| `id_consulta`  | BIGINT     | NOT NULL | FK `tb_consulta` |
| `id_medico`    | BIGINT     | NOT NULL | FK `tb_medico` (médico emissor) |
| `dt_emissao`   | TIMESTAMP  | NOT NULL | `DEFAULT NOW()` |
| `st_receita`   | CHAR(1)    | NOT NULL | `A` ativa, `I` inativa (reemitida) |

**Constraint chave**: `CREATE UNIQUE INDEX uq_rec_consulta_ativa ON hsg.tb_receita(id_consulta) WHERE st_receita = 'A'`.

Esse índice parcial Postgres garante **no máximo 1 receita ATIVA por consulta**, mas permite N inativas (histórico de reemissões).

Demais CHECKs:
- `ck_rec_status` — `st_receita IN ('A','I')`

Índices auxiliares:
- `idx_rec_consulta(id_consulta, dt_emissao DESC)` — buscas por consulta
- `idx_rec_medico(id_medico, dt_emissao DESC)` — relatórios por médico

---

## 2. Tabela `hsg.tb_receita_item`

| Coluna | Tipo | Nullable | Descrição |
|---|---|---|---|
| `id_receita_item` | BIGINT       | NOT NULL PK | Sequence `seq_receita_item` |
| `id_receita`      | BIGINT       | NOT NULL | FK `tb_receita` (CASCADE on delete) |
| `ds_medicamento`  | VARCHAR(300) | NOT NULL | Ex.: "Dipirona 500mg" |
| `ds_posologia`    | VARCHAR(500) | NOT NULL | Ex.: "1 cp via oral de 6/6h" |
| `ds_observacao`   | VARCHAR(1000)| NULL | Notas livres do médico |
| `ds_cid_10`       | VARCHAR(10)  | NULL | Ex.: "R51" |
| `nr_ordem`        | INTEGER      | NOT NULL | DEFAULT 1, CHECK `>= 1` |

Índice `idx_recitem_receita(id_receita, nr_ordem)` — preserva ordem na exibição.

`ON DELETE CASCADE` evita órfãos. Como a receita usa soft-delete (`st='I'`), o CASCADE só dispara em manutenção manual.

---

## 3. Entidades JPA

### [`Receita`](../../hsg-his-domain/src/main/java/br/com/hsg/domain/entity/Receita.java)

- `@OneToMany(mappedBy="receita", cascade=ALL, orphanRemoval=true)` para `List<ReceitaItem>` com `@OrderBy("ordem ASC")`
- `@ManyToOne LAZY` para `Consulta` e `Medico`
- Factory `emitir(consulta, medico, List<ReceitaItem>)` — vincula cada item à receita
- Método `inativar()` para soft-delete
- `getItens()` retorna `Collections.unmodifiableList`

### [`ReceitaItem`](../../hsg-his-domain/src/main/java/br/com/hsg/domain/entity/ReceitaItem.java)

- `@ManyToOne LAZY` para `Receita`
- Constantes `MAX_MEDICAMENTO=300`, `MAX_POSOLOGIA=500`, `MAX_OBSERVACAO=1000`, `MAX_CID_10=10`
- Factory `criar(medicamento, posologia, observacao, cid10, ordem)` valida:
  - medicamento obrigatório, ≤ 300
  - posologia obrigatória, ≤ 500
  - observacao opcional, ≤ 1000
  - cid10 opcional, ≤ 10 (uppercased no trim)
  - ordem `≥ 1`
- Método `vincular(Receita)` package-private — usado pela factory de `Receita`

---

## 4. Ciclo de vida

```
[emitir]                               ┌─ Receita nº 1  (st='A')  ──► PDF on-demand
   │                                   │
   ├─ Há receita ativa? ───[sim]─► inativar (st='I')   ──► histórico preservado
   │                                   │
   └─ persistir Receita nova ─────────► nova Receita (st='A')

[buscarParaPdf]
   └─ DAO retorna SOMENTE st='A'  →  PdfBuilder.build  →  byte[] → servlet
```

- O DAO `buscarAtivaPorConsulta(idConsulta)` faz `WHERE r.status = 'A'`, garantindo que reemissões antigas nunca aparecem na UI.
- Auditoria fica via `st_receita='I'` + `dt_emissao` — futuras consultas SQL podem reconstruir histórico de prescrições.

---

## 5. DAO

[`ReceitaDAO`](../../hsg-his-service/src/main/java/br/com/hsg/dao/ReceitaDAO.java):

- `salvar(Receita)` — persist
- `atualizar(Receita)` — merge (usado pra inativar)
- `buscarPorId(Long)` — find
- `buscarAtivaPorConsulta(Long)` — query com `JOIN FETCH` em itens + médico + especialidade + consulta + paciente (1 round-trip pra montar PDF)

---

## 6. Decisões importantes

| Decisão | Razão |
|---|-------|
| Soft-delete (`st_receita`) | Auditoria de reemissões + permite reverter manualmente em incidente clínico |
| UNIQUE parcial em vez de UNIQUE total | Permite múltiplas inativas (histórico) com apenas 1 ativa por vez |
| `posologia` separada de `medicamento` | Pesquisas farmacêuticas futuras (substituição de medicamento mantém posologia) |
| `cid_10` por item (não por receita) | Receita pode prescrever pra condições diferentes — flexibilidade clínica |
| PDF on-demand vs persistido | Economia de storage; permite troca de template/logo sem regenerar |
