# Módulo de Atendimento Clínico (Fase E)

Fluxo operacional do dia hospitalar: recepção realiza check-in, médico atende e registra observação, clínica/admin cancela ou marca falta.

## 1. Propósito

Fechar o ciclo do agendamento. Antes da Fase E, consultas ficavam paradas em `AGENDADA`, evoluindo apenas via auto-falta. Agora há ações manuais explícitas das equipes operacionais.

## 2. Ações

| Ação | Quem | Estado antes | Estado depois | Notificação |
|------|------|--------------|---------------|-------------|
| Check-in de chegada | Recepção (clinica/admin) | AGENDADA | CONFIRMADA | Médico |
| Atender (concluir) | Médico responsável | AGENDADA ou CONFIRMADA | REALIZADA + observação clínica | Paciente |
| Marcar falta manual | Recepção ou médico | AGENDADA ou CONFIRMADA | FALTOU | Paciente |
| Cancelar pela clínica | Recepção/admin/médico | AGENDADA ou CONFIRMADA | CANCELADA (slot liberado) | Paciente |

## 3. Camadas

- Migration: V29 adiciona `ds_observacao_clinica VARCHAR(1000)` em `tb_consulta`.
- Domain: `Consulta.observacaoClinica` + `marcarRealizadaComObservacao(String)` (valida não-vazio, ≤1000 chars e estado de origem).
- DAO: `ConsultaDAO.listarDoDia(inicio, fim, idMedicoOpcional)` (JOIN FETCH paciente/médico/especialidade/slot).
- Service: `ConsultaClinicaServiceFacade` + `ConsultaClinicaServiceImpl`:
  - `listarConsultasDoDia(LocalDate, Long)`
  - `listarConsultasDoDiaMedico(Long, LocalDate)`
  - `confirmarChegada(idConsulta, idResponsavel)`
  - `marcarRealizadaComObservacao(idConsulta, idMedico, observacao)`
  - `marcarFaltaPelaClinica(idConsulta, idResponsavel)`
  - `cancelarPelaClinica(idConsulta, idResponsavel, motivo)`
- Notificação: usa `NotificacaoEmissor` para emitir aos perfis envolvidos.

## 4. Telas

- `clinica/recepcao-dia.xhtml` (bean `RecepcaoDiaBean`)
- `clinica/minha-agenda.xhtml` (bean `MinhaAgendaBean`)

Detalhes em `docs/telas/clinica-recepcao-dia.md` e `docs/telas/clinica-minha-agenda.md`.

## 5. Regras

Detalhadas em `docs/regras/regras-negocio-agendamento.md` seção 7.1.

Resumo:
- Check-in só em consultas `AGENDADA`.
- Atender exige observação clínica não vazia e até 1000 caracteres; só o médico responsável pode marcar como realizada.
- Cancelamento pela clínica não tem trava de 24h (paciente continua tendo); exige motivo; libera o slot via lock pessimista.
- Falta manual aceita `AGENDADA` ou `CONFIRMADA`.

## 6. Notificações

Adicionadas ao mapeamento em `docs/modulos/notificacoes.md`:

| Ação | Destinatário | Tipo |
|------|--------------|------|
| Check-in | Médico | INFO |
| Atendimento concluído | Paciente | SUCESSO |
| Falta manual | Paciente | ALERTA |
| Cancelamento pela clínica | Paciente (com motivo) | ALERTA |

## 7. Testes

- `ConsultaMarcarRealizadaTest` (domain): observação obrigatória, limite 1000, estado de origem, idempotência sobre realizada.
- `ConsultaClinicaServiceImplTest` (service): cada ação valida estado/posse, persiste, libera slot quando cancelar, listagem do dia com filtro.

## 7.1 Histórico

Toda ação grava uma linha em `tb_consulta_historico`. Veja `docs/modulos/historico-consulta.md` para detalhes do modelo, regras e mapeamento.

## 8. Pontos abertos para evoluir

- Fila ordenada por chegada efetiva (hoje ordena por hora agendada).
- Histórico de mudanças (auditoria) — atualmente só `dataUltimaAtualizacao`.
- Reabertura de atendimento marcado como realizado (correção).
- Restrições por janela (ex.: check-in apenas ±2h do horário) — proposto, não implementado.
