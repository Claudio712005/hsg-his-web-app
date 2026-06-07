# Regras de Negócio — Agendamento de Consultas

Regras aplicadas pelo módulo de agendamento. Cada regra indica onde é validada (domínio, serviço ou banco) para rastreabilidade.

## 1. Grade médica (faixas)

| ID | Regra | Onde |
|----|-------|------|
| GR-01 | Médico, dia da semana, horários e duração são obrigatórios. | Domínio (`AgendaMedica.validar`) |
| GR-02 | Hora de início deve ser anterior à hora de fim. | Domínio |
| GR-03 | Duração do slot deve ser maior que zero (UI restringe a 5..240 min). | Domínio + UI |
| GR-04 | A faixa deve comportar ao menos um slot completo (duração da faixa >= duração do slot). | Domínio |
| GR-05 | Não pode haver duas faixas ativas sobrepostas no mesmo dia para o mesmo médico. | Serviço (`AgendaMedicaServiceImpl.validarSobreposicao` + `AgendaMedicaDAO.buscarSobreposicao`) |
| GR-06 | Sobreposição é revalidada ao criar, atualizar e reativar uma faixa. | Serviço |
| GR-07 | Faixas inativas não geram slots e não participam da validação de sobreposição. | Serviço |
| GR-08 | O dia da semana de uma faixa não é alterável após criação (UI desabilita; para mudar, inativar e criar outra). | UI |

## 2. Exceções de agenda

| ID | Regra | Onde |
|----|-------|------|
| EX-01 | Médico, datas e tipo são obrigatórios; data de início anterior à de fim. | Domínio (`AgendaMedicaExcecao.criar`) |
| EX-02 | Tipos válidos: FERIAS, BLOQUEIO, EVENTO. | Domínio (enum) + banco (CHECK) |
| EX-03 | Não pode haver exceções sobrepostas no período para o mesmo médico (qualquer combinação de tipos). | Serviço (`validarSobreposicaoExcecao` + `AgendaMedicaExcecaoDAO.buscarSobreposicao`) |
| EX-04 | Slots cujo intervalo cruza uma exceção vigente não são gerados. | Serviço (geração de slots) |

## 3. Geração de slots

| ID | Regra | Onde |
|----|-------|------|
| SL-01 | Período de geração entre 1 e 180 dias. | Serviço + UI |
| SL-02 | Para cada dia no intervalo, cada faixa ativa daquele dia da semana materializa slots de `duracao_min` em `duracao_min` até a hora de fim. | Serviço |
| SL-03 | Slots dentro de exceções vigentes são pulados. | Serviço |
| SL-04 | Geração idempotente: não recria slot já existente (verificação por médico+data de início e restrição única no banco). | Serviço + banco (UNIQUE id_medico, dt_inicio) |
| SL-05 | Geração em massa processa vários médicos; falha em um médico não interrompe os demais (resultado agregado com contagem de falhas). | Serviço/Bean |

## 4. Busca de horários (paciente)

| ID | Regra | Onde |
|----|-------|------|
| BU-01 | Especialidade e data são obrigatórias; médico é opcional. | Serviço (`ConsultaBuscaServiceImpl`) + Bean |
| BU-02 | Retorna apenas slots com status LIVRE. | DAO (`listarLivresPorEspecialidadeData`) |
| BU-03 | Considera apenas médicos vinculados à especialidade (relação N:N médico-especialidade). | DAO (subconsulta) |
| BU-04 | Não retorna horários no passado: o início efetivo da busca é o maior entre o começo do dia e o momento atual. | Serviço |

## 5. Agendamento efetivo (paciente)

| ID | Regra | Onde |
|----|-------|------|
| AG-01 | Reserva atômica: o slot é lido com lock pessimista (PESSIMISTIC_WRITE) antes de validar e reservar. | Serviço (`ConsultaServiceImpl.agendar` + `AgendaMedicaSlotDAO.buscarComLock`) |
| AG-02 | Proibição de overbooking: cada slot só pode ter uma consulta (restrição única em `id_agenda_slot`). | Banco (UNIQUE) |
| AG-03 | O slot precisa estar LIVRE no momento da reserva; caso contrário, o agendamento é recusado. | Serviço |
| AG-04 | Antecedência mínima de 2 horas em relação ao início do slot. | Serviço |
| AG-05 | Antecedência máxima de 90 dias. | Serviço |
| AG-06 | Limite de 3 consultas futuras ativas (AGENDADA ou CONFIRMADA) por paciente. | Serviço (`ConsultaDAO.contarFuturasAtivasPorPaciente`) |
| AG-07 | Ao reservar, o slot passa a RESERVADO e recebe o identificador da consulta; a consulta inicia com status AGENDADA. | Domínio (`AgendaMedicaSlot.reservar`, `Consulta.criar`) |

## 6. Convênio, carência e copagamento

O valor base da consulta vem de `Medico.valorConsulta`; quando ausente, usa valor padrão de 250,00.

| ID | Regra | Onde |
|----|-------|------|
| CV-01 | Para atendimento por convênio o paciente precisa de convênio ativo; ausência impede o convênio (particular permanece). | Serviço |
| CV-02 | A regra de cobertura usada é a do procedimento de consulta do plano: categoria "Consultas" ou procedimento contendo "consulta". | Serviço (`buscarRegraConsulta`) |
| CV-03 | Se o plano não cobre consultas, o atendimento é particular. | Serviço |
| CV-04 | Carência: liberação ocorre em `dataAdesao + carenciaDias`. Antes disso o procedimento está em carência. | Serviço (`estaEmCarencia`) |
| CV-05 | Em carência, o atendimento por convênio é bloqueado; o particular é oferecido. | Serviço |
| CV-06 | Copagamento por convênio = valor base x percentual de copagamento / 100 (arredondado a 2 casas, meio-para-cima). | Serviço |
| CV-07 | Cobertura do convênio = valor base - copagamento. | Serviço |
| CV-08 | Particular: o paciente paga o valor cheio; cobertura do convênio é zero. | Serviço |
| CV-09 | Snapshot financeiro (valor, copagamento, cobertura, tipo de atendimento e vínculo de convênio quando aplicável) é persistido na consulta no momento do agendamento. | Serviço + Domínio |

Comportamento de simulação versus agendamento:

- Simulação (preview na confirmação): tolerante. Se o convênio estiver indisponível ou em carência, retorna resultado particular com sinalizadores explicativos.
- Agendamento (efetivo): estrito. Se for solicitado convênio e ele estiver indisponível ou em carência, a operação é recusada com mensagem orientando agendar como particular.

## 7. Cancelamento (paciente)

| ID | Regra | Onde |
|----|-------|------|
| CA-01 | A consulta precisa pertencer ao paciente que solicita o cancelamento. | Serviço |
| CA-02 | Cancelamento permitido apenas com 24h ou mais de antecedência. | Serviço |
| CA-03 | Consultas já finalizadas (REALIZADA ou CANCELADA) não podem ser canceladas. | Domínio (`Consulta.cancelar`) |
| CA-04 | Motivo do cancelamento é obrigatório. | Domínio + Bean |
| CA-05 | Ao cancelar, o slot é liberado (volta a LIVRE) sob lock pessimista. | Serviço |

Observação: a clínica/administração pode cancelar sem a restrição de 24h (previsto para a Fase E, ainda não implementado).

## 7.1 Atendimento clínico (Fase E)

| ID | Regra | Onde |
|----|-------|------|
| AC-01 | Check-in só é permitido em consultas com status `AGENDADA`. | `ConsultaClinicaServiceImpl.confirmarChegada` |
| AC-02 | Marcar como realizada exige que o usuário seja o médico responsável pela consulta. | `ConsultaClinicaServiceImpl.marcarRealizadaComObservacao` |
| AC-03 | Observação clínica é obrigatória, com até 1000 caracteres. | Domínio (`Consulta.marcarRealizadaComObservacao`) |
| AC-04 | Estado de origem para marcar realizada: `AGENDADA` ou `CONFIRMADA`. | Domínio |
| AC-05 | Falta manual aceita `AGENDADA` ou `CONFIRMADA`. | Domínio (`Consulta.marcarFalta`) |
| AC-06 | Cancelamento pela clínica exige motivo e libera o slot via lock pessimista. Não há restrição de 24h. | `ConsultaClinicaServiceImpl.cancelarPelaClinica` |
| AC-07 | Cada ação dispara uma notificação in-app ao perfil oposto da ação (médico ↔ paciente). | `ConsultaClinicaServiceImpl` |
| AC-08 | **Check-in só pode ser feito por enfermeiro ou administrador.** Médicos e pacientes não podem confirmar chegada. | `ConsultaClinicaServiceImpl.confirmarChegada` |
| AC-09 | **Médicos só atuam nas próprias consultas.** Marcar falta ou cancelar consulta de outro médico é recusado com `IllegalStateException`. | `ConsultaClinicaServiceImpl.validarMedicoSoNasProprias` |
| AC-10 | Toda ação é registrada em `tb_consulta_historico` com tipo de responsável (PACIENTE, MEDICO, ENFERMEIRO, ADMIN, SISTEMA), ação, data e observação opcional. Auto-falta usa `SISTEMA`. | `ConsultaClinicaServiceImpl.registrarHistorico` + `ConsultaServiceImpl.registrarHistoricoSeguro` + `ConsultaAutoFaltaServiceImpl.registrarHistorico` |
| AC-11 | Notificação in-app ao médico no check-in é obrigatória (e-mail não é enviado nesse caso). | `ConsultaClinicaServiceImpl.confirmarChegada` |
| AC-12 | Falha ao gravar histórico não desfaz a ação principal; apenas registra log WARNING. | Idem |

## 8. Status

### 8.1 Status da consulta (`StatusConsulta`)

AGENDADA, CONFIRMADA, REALIZADA, CANCELADA, FALTOU.
Transições válidas: AGENDADA -> CONFIRMADA -> REALIZADA; AGENDADA/CONFIRMADA -> FALTOU; AGENDADA/CONFIRMADA -> CANCELADA.

### 8.2 Status do slot (`StatusSlotAgenda`)

LIVRE, RESERVADO, BLOQUEADO, CANCELADO.
Reserva exige LIVRE. Bloqueio não é permitido em slot RESERVADO (cancelar a consulta antes).

## 8.3 Auto-falta (job agendado)

| ID | Regra | Onde |
|----|-------|------|
| AF-01 | Consulta com `status IN (AGENDADA, CONFIRMADA)` e `dataConsulta < agora - 24h` é marcada como `FALTOU`. | `ConsultaAutoFaltaServiceImpl` |
| AF-02 | Após marcar, e-mail é enviado ao médico informando o ocorrido (orienta a corrigir manualmente caso a consulta tenha sido realizada). | `MailService.enviarFaltaAutomaticaParaMedico` |
| AF-03 | Médico sem e-mail registrado: consulta ainda é marcada como falta; o envio é pulado e registrado em log. | `ConsultaAutoFaltaServiceImpl` |
| AF-04 | Falha em uma consulta (ex.: status já final) não interrompe o processamento das demais. | `ConsultaAutoFaltaServiceImpl` |
| AF-05 | O job roda diariamente às 02:00 (horário do servidor) via `@Schedule` em `ConsultaAutoFaltaTimer`. | `ConsultaAutoFaltaTimer` |

Documentação completa em `docs/modulos/agendamento-schedules.md`.

## 8.4 Anexos e exames (módulo storage)

| ID | Regra | Onde |
|----|-------|------|
| AX-01 | Content-Type deve estar na whitelist (`application/pdf`, `image/jpeg`, `image/png`, `image/webp`) e os magic bytes do arquivo precisam bater com o tipo declarado. | `StorageGuard.validarContentType` + `StorageGuard.validarMagicBytes` |
| AX-02 | Tamanho do arquivo ≤ `APP_STORAGE_MAX_BYTES` (default 20 MB). | `StorageGuard.validarTamanho` |
| AX-03 | Médico só pode anexar em consultas/anotações nas quais é o médico responsável. | `ArquivoServiceImpl.anexarEmConsulta` / `anexarEmAnotacao` |
| AX-04 | Paciente só anexa exame em consulta **própria** e somente em status `AGENDADA` ou `CONFIRMADA`. | `ArquivoServiceImpl.anexarExameEmConsulta` |
| AX-05 | Consulta com status `CANCELADA` não aceita novos anexos. | `ArquivoServiceImpl.bloquearSeCancelada` |
| AX-06 | Filename é sanitizado (remove path separators, controle, normaliza acentuação, evita `.` à esquerda). | `StorageGuard.sanitizeFilename` |

Regras de autorização de leitura (download/visualização):
- `ADMIN` e `ENFERMEIRO`: qualquer arquivo
- `MEDICO`: só de consultas/anotações próprias
- `PACIENTE`: só de consultas/exames próprios

Regras de remoção: autor original do upload (mesmo `id_responsavel` + `tp_responsavel`) **ou** `ADMIN`. Remoção é soft-delete (`st_arquivo='I'`), GC físico posterior.

Documentação completa em `docs/modulos/storage-arquivos.md` e `docs/dominio/modelo-arquivo.md`. ADRs relacionadas: [ADR-008 buckets separados](../adrs/ADR008-storage-buckets-separados.md), [ADR-009 path lógico vs URL](../adrs/ADR009-path-logico-vs-url.md).

## 9. Constantes operacionais

| Constante | Valor | Local |
|-----------|-------|-------|
| Antecedência mínima | 2 horas | `ConsultaServiceImpl` |
| Antecedência máxima | 90 dias | `ConsultaServiceImpl` |
| Limite de consultas futuras ativas | 3 | `ConsultaServiceImpl` |
| Janela de cancelamento pelo paciente | 24 horas | `ConsultaServiceImpl` |
| Valor padrão da consulta | 250,00 | `ConsultaServiceImpl` |
| Período de geração de slots | 1 a 180 dias | `AgendaMedicaServiceImpl` |
| Duração de slot (UI) | 5 a 240 minutos | UI |
| Tolerância para auto-falta | 24 horas | `ConsultaAutoFaltaServiceImpl` |
| Execução do job de auto-falta | Diária às 02:00 | `ConsultaAutoFaltaTimer` |
| Tamanho máximo de anexo | 20 MB (configurável) | `APP_STORAGE_MAX_BYTES` |
| TTL de URL pré-assinada | 15 min (configurável) | `APP_STORAGE_PRESIGN_TTL_MIN` |
