# Tela: Minha Agenda (Médico)

Caminho: `clinica/minha-agenda.xhtml`
Bean: `MinhaAgendaBean` (`@ViewScoped`, `@Named("minhaAgendaBean")`)
Perfil: clínica.MEDICO (link no menu só aparece para o tipo MEDICO)

## 1. Objetivo

Painel de produção do médico: ver as consultas do dia, marcar atendimentos como realizados (com observação clínica) e registrar faltas.

## 2. Estrutura

- Filtro: data (padrão hoje).
- KPIs: Total, Aguardando, Em espera (confirmadas), Realizadas, Faltas.
- Lista cronológica das consultas do médico logado:
  - Hora, paciente, especialidade, chip de status.
  - Para consultas `REALIZADA` exibe a observação clínica registrada.

## 3. Ações por consulta

| Ação | Visível quando | Efeito |
|------|----------------|--------|
| Atender | `AGENDADA` ou `CONFIRMADA` | Abre diálogo para informar a observação clínica e marca como `REALIZADA` |
| Falta | `AGENDADA` ou `CONFIRMADA` | Marca a consulta como `FALTOU` |

## 4. Diálogo de atendimento

- Campo `textarea` para observação clínica (obrigatório, ≤1000 caracteres). Placeholder sugere registrar queixa, exame e conduta.
- Ao confirmar, o serviço valida estado, posse (médico responsável) e tamanho.

## 5. Mensagens e validações

- `p:growl` no topo para feedback.
- Erros propagados com mensagem do domínio/serviço:
  - "A observação clínica é obrigatória."
  - "Apenas o médico responsável pela consulta pode marcá-la como realizada."
  - "Apenas consultas agendadas ou confirmadas podem ser marcadas como realizadas."

## 6. Observações

- O id do médico vem de `BeanSessao.usuarioClinica.id` (filtra na sessão para evitar manipulação do client).
- Após cada ação a lista é recarregada para refletir o estado.
- A observação clínica é exibida em destaque verde nos itens já realizados.
