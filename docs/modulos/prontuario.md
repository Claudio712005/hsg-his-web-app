# Prontuário do Paciente

Visão **única e read-only** de tudo que existe sobre 1 paciente cross-consulta. Não cria nova fonte de verdade — agrega dados já persistidos (consultas, anotações, anexos, receitas, alergias).

---

## 1. Princípios

- **Sem nova entidade** — apenas DTO `ProntuarioDTO` agregador
- **Read-only** — qualquer edição é feita nos fluxos existentes (anotação na consulta, alergia no módulo dedicado etc)
- **PDF on-demand** — exportação via servlet, sem persistir bytes
- **Autorização gradativa** — paciente vê só o próprio; equipe clínica (médico/enfermeiro/admin) vê qualquer paciente sob justificativa clínica
- **Mascaramento** — CPF e carteirinha sempre mascarados na tela e no PDF

---

## 2. Conteúdo (MVP)

| Seção | Fonte |
|-------|-------|
| Cabeçalho | `tb_pac` (nome, dt nasc, idade calculada, email, telefone, CPF mascarado) |
| Alergias ativas | `tb_alergia` (status ≠ EXCLUIDA) |
| Linha do tempo de consultas | `tb_consulta` ordenado por `dt_consulta DESC` |
| Por consulta | anotações (`tb_consulta_anotacao`) + anexos (`tb_arquivo` ativos) + receita ativa + receitas inativas (accordion) |
| Convênio ativo | *(stub futuro)* |
| Sinais vitais | *(stub futuro)* |
| Antecedentes manuais | *(stub futuro)* |

---

## 3. Telas

| Rota | Quem | Como chega |
|------|------|------------|
| `clinica/prontuario.xhtml` | médico / enfermeiro / admin | Item de menu "Prontuário" no header clínico **ou** botão "Ver prontuário" no diálogo de anotações (recepcao + minha-agenda), com `?idPaciente=N` |
| `paciente/meu-prontuario.xhtml` | paciente | Item de menu "Meu Prontuário" no sidebar paciente |

Fragment compartilhado: [`WEB-INF/templates/fragments/prontuario-corpo.xhtml`](../../hsg-his-web/src/main/webapp/WEB-INF/templates/fragments/prontuario-corpo.xhtml). Recebe `bean` (`ProntuarioBean` ou `MeuProntuarioBean`) + `contexto` (`CLINICO`/`PACIENTE`) via `<ui:param>`.

UI: `<p:accordionPanel multiple="true">` por consulta. Cada tab traz observação clínica, anotações, anexos (com botão baixar), receita ativa (botão PDF) e receitas inativas (accordion aninhado).

---

## 4. Busca de paciente (clínico)

`<p:autoComplete>` com `completeMethod="#{prontuarioBean.autocompletarPaciente}"`, `minQueryLength="2"`, `queryDelay="350"`. Pesquisa por LIKE em `primeiroNome`/`sobrenome`/`nomeCompleto`, retorna até 10 sugestões. Limitada a usuários clínicos (paciente recebe `IllegalStateException`).

---

## 5. Regras PR-*

| ID | Regra | Onde |
|----|-------|------|
| PR-01 | Apenas PACIENTE (próprio), MEDICO, ENFERMEIRO ou ADMIN acessam o prontuário | `ProntuarioServiceImpl.autorizar` |
| PR-02 | Qualquer médico pode acessar qualquer paciente (sem filtro "já atendeu") | Idem |
| PR-03 | Receitas inativas aparecem listadas em accordion separado com badge "Inativa" | `prontuario-corpo.xhtml` |
| PR-04 | Anexos com `st_arquivo='I'` não aparecem | `ProntuarioServiceImpl.montarAnexos` |
| PR-05 | CPF mascarado como `***.***.***-**`; carteirinha de convênio mascarada (planejado) | `ProntuarioServiceImpl.montarResumoPaciente` |
| PR-06 | Busca de paciente proibida para PACIENTE e SISTEMA | `ProntuarioServiceImpl.buscarPacientes` |

---

## 6. Geração de PDF

Lib: **OpenPDF 1.3.30** (mesma do receituário). Classe [`ProntuarioPdfBuilder`](../../hsg-his-service/src/main/java/br/com/hsg/service/impl/clinica/ProntuarioPdfBuilder.java).

Estrutura:
1. Cabeçalho com logo HSG + data de emissão
2. Bloco PACIENTE
3. Bloco ALERGIAS (lista com tipo + gravidade + status + reação)
4. Bloco HISTÓRICO DE CONSULTAS — para cada consulta: data, status, médico, especialidade, observação clínica, anotações, anexos, receita ativa, contagem de receitas inativas
5. Rodapé com nota de emissão eletrônica

Servlet: [`/prontuario/pdf?idPaciente=N`](../../hsg-his-web/src/main/java/br/com/hsg/web/servlet/ProntuarioPdfServlet.java). Se paciente logado e não passa `idPaciente`, usa o próprio ID. Erros: 400/401/403/404/500.

---

## 7. Service

[`ProntuarioServiceFacade`](../../hsg-his-service/src/main/java/br/com/hsg/service/facade/clinica/ProntuarioServiceFacade.java):

- `montarParaPaciente(idPaciente, idSolic, tpSolic)` → `ProntuarioDTO`
- `buscarPacientes(termo, idSolic, tpSolic, limite)` → `List<PacienteBuscaDTO>`

Implementação [`ProntuarioServiceImpl`](../../hsg-his-service/src/main/java/br/com/hsg/service/impl/clinica/ProntuarioServiceImpl.java) reutiliza DAOs existentes (sem novos DAOs além de extensões em `PacienteDAO.buscarPorTermo` e `ReceitaDAO.listarTodasPorConsulta`).

---

## 8. DTOs

| DTO | Conteúdo |
|-----|----------|
| `ProntuarioDTO` | root: paciente + alergias + consultas |
| `PacienteResumoDTO` | id, nome, dt nasc, idade, sexo (futuro), CPF mascarado, contatos, convênio (futuro) |
| `AlergiaResumoDTO` | id, nome, tipo, gravidade, status, reação |
| `ConsultaResumoDTO` | id, dt consulta, médico, status, observação clínica, motivo cancelamento, anotações, anexos, receita ativa, receitas inativas |
| `AnotacaoResumoDTO` | título, descrição, data, responsável |
| `AnexoResumoDTO` | nome, content-type, tamanho, domínio, data upload, responsável |
| `ReceitaResumoDTO` | id, data emissão, flag ativa, lista de itens (medicamento + posologia + observação + CID-10) |

Pacote `service/dto/prontuario/`.

---

## 9. Testes

| Classe | Testes |
|--------|--------|
| `ProntuarioServiceImplTest` | 12 (autorização, busca, casos de erro) |
| `ProntuarioPdfBuilderTest` | 4 (PDF válido, sem dados, dto null, sem paciente) |

---

## 10. Pontos abertos

- **Convênio ativo** no cabeçalho — depende de `tb_pac_conv` query; deixar pra próxima iteração
- **Sinais vitais** — novo módulo `tb_paciente_sinais_vitais` (a definir escopo)
- **Antecedentes manuais** (cirurgias, doenças crônicas, histórico familiar) — novo módulo
- **Filtro de período** na timeline (últimos 30d, 6m, 1a, tudo) — UX futura
- **Editor de anotações longitudinais** (sem vincular consulta específica) — futuro
- **Compartilhamento controlado** (paciente gera link temporário pra outro médico) — futuro
