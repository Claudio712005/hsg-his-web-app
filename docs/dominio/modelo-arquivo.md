# Modelo de Dados — Arquivo

Schema, factory e ciclo de vida da entidade `Arquivo` (módulo storage).

---

## 1. Tabela `hsg.tb_arquivo`

Migration: [`V32__create_arquivo.sql`](../../infra/db/migrations/V32__create_arquivo.sql)

| Coluna | Tipo | Nullable | Descrição |
|---|---|---|---|
| `id_arquivo`       | BIGINT       | NOT NULL PK | Sequence `seq_arquivo` |
| `ds_path_logico`   | VARCHAR(500) | NOT NULL UNIQUE | Path lógico vendor-agnostic (`/<prefixo>/<owner>/<yyyy>/<MM>/<uuid>.<ext>`) |
| `ds_dominio`       | VARCHAR(30)  | NOT NULL | Enum `StorageDomain` (CHECK) |
| `ds_nome_original` | VARCHAR(255) | NOT NULL | Filename sanitizado |
| `ds_content_type`  | VARCHAR(100) | NOT NULL | MIME validado pela whitelist |
| `nr_tamanho_bytes` | BIGINT       | NOT NULL | > 0 (CHECK) |
| `ds_sha256`        | VARCHAR(64)  | NULL | Hex digest, opcional (usado para integridade/dedup futuro) |
| `id_consulta`      | BIGINT       | NULL | FK `tb_consulta` |
| `id_anotacao`      | BIGINT       | NULL | FK `tb_consulta_anotacao` |
| `id_paciente`      | BIGINT       | NULL | FK `tb_pac` |
| `id_responsavel`   | BIGINT       | NOT NULL | ID do uploader |
| `tp_responsavel`   | VARCHAR(12)  | NOT NULL | `MEDICO/ENFERMEIRO/ADMIN/PACIENTE` |
| `dt_upload`        | TIMESTAMP    | NOT NULL | `DEFAULT NOW()` |
| `st_arquivo`       | CHAR(1)      | NOT NULL | `A` ativo, `I` inativo (soft-delete) |

CHECKs:
- `ck_arq_dominio` — `ds_dominio IN ('ANEXO_CLIENTE','ANEXO_CONSULTA','ANEXO_ANOTACAO','EXAME_CONSULTA')`
- `ck_arq_resp` — `tp_responsavel IN ('MEDICO','ENFERMEIRO','ADMIN','PACIENTE')`
- `ck_arq_tamanho` — `nr_tamanho_bytes > 0`
- `ck_arq_status` — `st_arquivo IN ('A','I')`
- `ck_arq_target` — pelo menos um de (`id_consulta`, `id_anotacao`, `id_paciente`) NOT NULL

Índices:
- `idx_arq_consulta(id_consulta, dt_upload DESC)`
- `idx_arq_anotacao(id_anotacao, dt_upload DESC)`
- `idx_arq_paciente(id_paciente, dt_upload DESC)`
- `idx_arq_dominio(ds_dominio, st_arquivo)` — usado pelo job de GC

---

## 2. Entidade JPA

[`Arquivo`](../../hsg-his-domain/src/main/java/br/com/hsg/domain/entity/Arquivo.java) com:
- Constantes: `MAX_PATH_LOGICO=500`, `MAX_NOME_ORIGINAL=255`, `MAX_CONTENT_TYPE=100`, `MAX_SHA256=64`
- Construtor `protected`
- Factory: `registrar(StorageDomain dominio, long ownerId, String pathLogico, String nomeOriginal, String contentType, long tamanhoBytes, String sha256, Long idResponsavel, TipoResponsavel tipoResponsavel)`
- Método `inativar()` — soft-delete

A factory distribui o `ownerId` para a coluna FK correta com base no domínio:

| Domínio | Coluna preenchida |
|---|---|
| `ANEXO_CLIENTE`   | `id_paciente` |
| `ANEXO_CONSULTA`  | `id_consulta` |
| `EXAME_CONSULTA`  | `id_consulta` |
| `ANEXO_ANOTACAO`  | `id_anotacao` |

Tipo responsável validado contra `EnumSet.of(MEDICO, ENFERMEIRO, ADMIN, PACIENTE)` — `SISTEMA` não anexa.

---

## 3. Ciclo de vida

```
[upload]  →  registrar()  →  st_arquivo='A'
                                 │
                                 ├─ inativar()  →  st_arquivo='I'
                                 │                       │
                                 │                       └─ GC job (futuro) → delete físico no bucket
                                 │
                                 └─ atualizar metadata (não há edição hoje)
```

- Soft-delete preserva auditoria.
- Versioning do bucket S3/MinIO garante recuperação se delete físico for acidental.
- GC roda em job periódico (a implementar) que lê `ArquivoDAO.listarInativosParaGc(limite)`, faz `storage.delete(path)`, depois remove a linha.

---

## 4. Enum `StorageDomain`

[`StorageDomain`](../../hsg-his-domain/src/main/java/br/com/hsg/domain/enums/StorageDomain.java) — quatro valores com `prefixoLogico`:

| Valor | Prefixo lógico |
|---|---|
| `ANEXO_CLIENTE`  | `/anexos/cliente`  |
| `ANEXO_CONSULTA` | `/anexos/consulta` |
| `ANEXO_ANOTACAO` | `/anexos/anotacao` |
| `EXAME_CONSULTA` | `/exames/consulta` |

Método estático `pelaPrefixoDoPathLogico(path)` faz roundtrip reverso usado pelo `StoragePathResolver.resolve` para descobrir qual bucket está dono do path.

---

## 5. Relacionamentos

Não há `@ManyToOne` JPA — usa FKs por `Long` simples (`id_consulta`, `id_anotacao`, `id_paciente`). Motivação:

- Evita carga lazy desnecessária ao listar anexos
- Facilita o factory polimórfico (qual FK preencher depende do domínio)
- Constraint `ck_arq_target` no DB garante que pelo menos um FK está populado

Para navegar até a entidade dona, os serviços fazem lookup explícito via DAO próprio.
