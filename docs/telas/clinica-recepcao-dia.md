# Tela: Recepção do Dia

Caminho: `clinica/recepcao-dia.xhtml`
Bean: `RecepcaoDiaBean` (`@ViewScoped`, `@Named("recepcaoDiaBean")`)
Perfis: clínica (médico ou enfermeiro) e administração

## 1. Objetivo

Operar o fluxo da agenda do dia: confirmar a chegada dos pacientes (check-in), marcar faltas e cancelar consultas quando necessário.

## 2. Estrutura

- Filtros (sticky): data (padrão hoje) e médico opcional. Mudança dispara recarga via ajax.
- KPIs: Total do dia, Aguardando, Confirmadas, Realizadas, Faltas, Canceladas.
- Lista de consultas do dia: cada linha mostra hora, paciente, médico, especialidade e chip de status.
- Cor de borda esquerda do card indica estado: azul (AGENDADA), verde (CONFIRMADA), cinza (REALIZADA/FALTOU/CANCELADA).

## 3. Ações por consulta

| Ação | Visível quando | Efeito |
|------|----------------|--------|
| Check-in | Status `AGENDADA` | Muda para `CONFIRMADA` e notifica o médico |
| Falta | Status `AGENDADA` ou `CONFIRMADA` | Muda para `FALTOU` e notifica o paciente |
| Cancelar | Status `AGENDADA` ou `CONFIRMADA` | Diálogo solicita motivo; cancela, libera slot e notifica o paciente |

## 4. Mensagens

- `p:growl` no topo.
- Erros do serviço (estado inválido, consulta não encontrada) são propagados com a mensagem real.

## 5. Observações

- Lista é recarregada após cada ação para refletir o novo estado.
- O serviço usa `LEFT JOIN FETCH` no DAO para evitar lazy load nas linhas renderizadas.
- Cancelamento aciona `buscarComLock` no slot para liberá-lo com segurança em concorrência.
