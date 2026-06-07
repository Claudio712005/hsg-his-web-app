# Receituário Médico

Emissão de receitas simples vinculadas à consulta. PDF gerado **on-demand** a partir do template padrão do sistema — não há objeto físico persistido no storage, só metadata leve no DB.

---

## 1. Princípios de design

- **Sem storage de PDF**: economia de bytes + permite alterar template/branding sem reprocessar histórico
- **1 receita ativa por consulta** (UNIQUE parcial `WHERE st_receita='A'`)
- **Reemissão preserva auditoria**: anterior recebe `st_receita='I'`, nova é inserida
- **Escopo MVP**: apenas receitas simples. Receitas controladas/especiais (notificação Anvisa C1, B1, retenção) continuam sendo entregues fisicamente pelo médico — aviso visível na UI
- **Logo da clínica embarcado no PDF** via classpath (`branding/hsg-logo-completa.png`)

---

## 2. Modelo

Detalhes em `docs/dominio/modelo-receita.md`.

- `tb_receita(id, id_consulta, id_medico, dt_emissao, st_receita)` — UNIQUE parcial em `id_consulta` quando ativa
- `tb_receita_item(id, id_receita, ds_medicamento, ds_posologia, ds_observacao, ds_cid_10, nr_ordem)`

Entidades: [`Receita`](../../hsg-his-domain/src/main/java/br/com/hsg/domain/entity/Receita.java), [`ReceitaItem`](../../hsg-his-domain/src/main/java/br/com/hsg/domain/entity/ReceitaItem.java).

Factory `Receita.emitir(consulta, medico, List<ReceitaItem>)` vincula os itens.

---

## 3. Regras RC-*

| ID | Regra | Onde |
|----|-------|------|
| RC-01 | Apenas o médico responsável da consulta emite | `ReceituarioServiceImpl.emitir` |
| RC-02 | Consulta `CANCELADA` ou `FALTOU` não aceita receita | Idem |
| RC-03 | Mínimo 1 item; `medicamento` (1-300) e `posologia` (1-500) obrigatórios; `observacao` opcional (0-1000); `cid10` opcional (0-10) | `ReceitaItem.criar` + `ReceituarioServiceImpl` |
| RC-04 | Reemissão inativa a anterior (`st_receita='I'`) e cria nova ativa | `ReceituarioServiceImpl.emitir` |
| RC-05 | Leitura/download: ADMIN, ENFERMEIRO sempre. MEDICO só nas próprias. PACIENTE só na própria consulta | `ReceituarioServiceImpl.autorizar` |

---

## 4. Geração do PDF

Lib: **OpenPDF 1.3.30** (fork iText 4, Apache 2.0). Classe [`ReceitaPdfBuilder`](../../hsg-his-service/src/main/java/br/com/hsg/service/impl/clinica/ReceitaPdfBuilder.java).

Estrutura do PDF (A4):
1. Cabeçalho: logo HSG (lockup horizontal) + nome/endereço/contato da clínica
2. Título "RECEITUÁRIO MÉDICO" + data emissão + número da receita
3. Bloco PACIENTE: nome completo
4. Bloco MÉDICO RESPONSÁVEL: nome + CRM/UF formatado
5. Bloco PRESCRIÇÃO: lista numerada de medicamentos, com posologia, observação (se houver) e CID-10 (se houver)
6. Linha de assinatura com nome/CRM do médico
7. Rodapé: nota de emissão eletrônica

Logo embarcado pelo classpath em [`hsg-his-service/src/main/resources/branding/hsg-logo-completa.png`](../../hsg-his-service/src/main/resources/branding/hsg-logo-completa.png).

---

## 5. Servlet de download

[`/receita/pdf?idConsulta=N`](../../hsg-his-web/src/main/java/br/com/hsg/web/servlet/ReceitaPdfServlet.java):

1. Lê `idConsulta` do query param
2. Resolve identidade do solicitante via `BeanSessao`
3. Chama `ReceituarioServiceFacade.buscarParaPdf(idConsulta, idResp, tpResp)` — aplica RC-05
4. `ReceitaPdfBuilder.build(receita)` → byte[]
5. `Content-Type: application/pdf`, `Content-Disposition: inline; filename="receita-N.pdf"`

Erros: 400 (param inválido), 401 (sem sessão), 403 (sem permissão), 404 (sem receita ativa), 500 (falha PDF).

---

## 6. UI

### Médico (`clinica/minha-agenda.xhtml`)

No diálogo de anotações (`dlgAnotacoesMA`), bloco **Receituário**:

- Aviso fixo destacando que apenas receitas simples são suportadas pelo sistema
- Se já há receita ativa: card read-only com lista dos itens + botões "Baixar PDF" e "Reemitir"
- Se ainda não há (e médico pode emitir): formulário com itens dinâmicos (medicamento + posologia + observação + CID-10), botão "Adicionar medicamento", botão "Emitir receituário"
- Reemitir copia itens da receita anterior pro formulário e exige clicar "Emitir receituário" novamente

### Enfermeiro / Admin (`clinica/recepcao-dia.xhtml`)

Mesmo bloco, mas somente leitura: card read-only + botão "Baixar PDF". Sem botão de emitir/reemitir.

### Paciente (`paciente/minhas-consultas.xhtml`)

Ícone de prescrição (`fa fa-prescription`) na coluna Ações da tabela de consultas, visível apenas quando `temReceita(c)` retorna `true`. Abre PDF em nova aba.

---

## 7. Aviso ao usuário (texto exibido)

> ⚠ Apenas **receitas simples** podem ser emitidas e baixadas pelo sistema. Prescrições mais complexas (receitas controladas, especiais, com retenção) devem ser entregues fisicamente pelo médico na consulta.

Componente: `<h:panelGroup styleClass="note">` com fundo amarelo claro, fixo no topo do bloco Receituário em ambas as telas clínicas.

---

## 8. Testes

| Classe | Testes |
|--------|--------|
| `ReceitaItemTest` (domínio) | 10 |
| `ReceitaTest` (domínio) | 7 |
| `ReceitaDAOTest` (service) | 7 |
| `ReceituarioServiceImplTest` (service) | 19 |
| `ReceitaPdfBuilderTest` (service) | 3 |

Total: 46 novos testes unitários.

---

## 9. Massa de seed (DEV)

3 receitas em consultas REALIZADA (`infra/db/seed/init.sql`):

| Consulta | Médico × Paciente | Itens |
|----------|-------------------|-------|
| C3 (há 7d) | dr.roberto × claudio.filho | Dipirona 500mg + Hidratação oral |
| C9 (há 2d) | dr.roberto × carla.silva | SRO (sais de reidratação) |
| C12 (há 3d) | dra.fernanda × mariana.santos | Amitriptilina 25mg + Analgésico SOS |

---

## 10. Pontos abertos / evolução

- **Receitas controladas (notificação Anvisa)**: fora do MVP. Exige numeração sequencial controlada por farmácia, retenção de 2ª via, integração com sistemas reguladórios.
- **Assinatura digital ICP-Brasil**: o PDF tem espaço para assinatura manual. Assinatura digital qualificada (e-CPF médico) é evolução natural.
- **CID-10 com autocomplete**: hoje é input livre. Próximo passo: tabela `tb_cid_10` + `p:autoComplete`.
- **Receita por especialidade** (templates diferentes): hoje template único. Especialização vinda em breve se demandar.
