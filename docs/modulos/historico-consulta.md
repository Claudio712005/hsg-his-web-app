# Histórico de Consulta

Trilha de auditoria das mudanças de estado da consulta. Cada ação registra uma linha em `tb_consulta_historico` com responsável, papel, ação, data e observação opcional.

## 1. Propósito

- Permitir auditoria de quem fez o quê em cada consulta.
- Comprovar respeito às regras (médico não atuou em consulta alheia, check-in feito por enfermeiro etc.).
- Base para futuros relatórios operacionais (tempo médio entre agendamento e atendimento, taxa de auto-falta vs. falta manual, etc.).

## 2. Modelo

Tabela `TB_CONSULTA_HISTORICO` (migration **V30**):

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id_consulta_historico` | BIGINT | PK |
| `id_consulta` | BIGINT FK | Consulta referenciada |
| `tp_acao` | VARCHAR(15) | `AGENDADA`, `CHECK_IN`, `REALIZADA`, `FALTOU`, `CANCELADA` |
| `id_responsavel` | BIGINT | Id do responsável no perfil (nulo p/ SISTEMA) |
| `tp_responsavel` | VARCHAR(12) | `PACIENTE`, `MEDICO`, `ENFERMEIRO`, `ADMIN`, `SISTEMA` |
| `ds_observacao` | VARCHAR(1000) | Observação clínica, motivo de cancelamento, motivo do sistema etc. |
| `dt_acao` | TIMESTAMP | Quando ocorreu |

Índices: `(id_consulta, dt_acao DESC)`, `(tp_acao)`.

Entidade JPA: `ConsultaHistorico` com factory `registrar(consulta, acao, idResponsavel, tipoResponsavel, observacao)`.

Enums: `AcaoConsulta`, `TipoResponsavel`.

## 3. Registros disparados

| Ação no sistema | AcaoConsulta | TipoResponsavel | Observação |
|------------------|--------------|------------------|------------|
| Paciente agenda consulta | `AGENDADA` | `PACIENTE` | nulo |
| Paciente cancela consulta | `CANCELADA` | `PACIENTE` | motivo |
| Enfermeiro/admin faz check-in | `CHECK_IN` | `ENFERMEIRO`/`ADMIN` | nulo |
| Médico marca realizada | `REALIZADA` | `MEDICO` | observação clínica |
| Médico marca falta | `FALTOU` | `MEDICO` | nulo |
| Recepção marca falta | `FALTOU` | `ENFERMEIRO`/`ADMIN` | nulo |
| Recepção/médico cancela | `CANCELADA` | `MEDICO`/`ENFERMEIRO`/`ADMIN` | motivo |
| Auto-falta scheduler | `FALTOU` | `SISTEMA` | "Auto-falta após 24h de tolerância" |

## 4. Regras de acesso aplicadas pelo serviço

- **Check-in (AC-08):** somente `ENFERMEIRO` ou `ADMIN`. Médico ou paciente → `IllegalStateException`.
- **Médico nas próprias consultas (AC-09):** ao chamar `marcarFaltaPelaClinica` ou `cancelarPelaClinica` com `TipoResponsavel.MEDICO`, o serviço verifica `c.getMedico().getId() == idResponsavel`. Tentar agir em consulta de outro médico → recusado.
- **Marcar realizada:** continua exigindo que `idMedico` informado seja o médico responsável (regra pré-existente AC-02), e o histórico registra `MEDICO` + observação clínica.

## 5. Notificação in-app ao check-in

No `confirmarChegada` o serviço emite notificação **in-app** (`NotificacaoEmissor.emitir(TipoDestinatarioNotificacao.MEDICO, ...)`) com tipo `INFO` e categoria `CONSULTA`. Não há disparo de e-mail nesse caso, conforme requisito operacional.

## 6. Falhas no histórico

`registrarHistorico` está em try/catch (em todos os pontos de chamada): qualquer falha de persistência é logada em WARNING e não desfaz a ação de negócio. Isso evita que problemas na trilha de auditoria bloqueiem operações clínicas.

## 7. Como consultar

- Service: `ConsultaClinicaServiceFacade.historicoPorConsulta(idConsulta)` retorna `List<ConsultaHistorico>` ordenado por data desc.
- DAO direto: `ConsultaHistoricoDAO.listarPorConsulta(idConsulta)`.
- UI: tela de detalhamento da consulta poderá no futuro exibir essa linha do tempo (não implementado ainda).

## 8. Testes

`ConsultaClinicaServiceImplTest` (atualizado) cobre:

- Check-in feito por médico ou paciente é recusado.
- Check-in feito por enfermeiro persiste e registra histórico.
- Marcar realizada registra histórico.
- Médico não pode marcar falta/cancelar consulta de outro médico.
- Cancelamento persiste histórico além de liberar slot.
