# Modelo de Dados — Agendamento

Entidades, tabelas e migrations do módulo de agendamento. Schema único `hsg`.

## 1. Entidades

### 1.1 MedicoEspecialidade

Vínculo N:N entre médico e especialidade, com indicação de especialidade principal.

- Tabela: `TB_MEDICO_ESPECIALIDADE`
- Campos: `id`, `medico`, `especialidade`, `principal` (CHAR(1) 'S'/'N'), `dataCadastro`
- Restrição única: (`id_medico`, `id_especialidade`)
- Factory: `criar(medico, especialidade, principal)`; métodos `isPrincipal`, `marcarComoPrincipal`, `desmarcarPrincipal`

### 1.2 AgendaMedica (faixa)

Grade semanal de atendimento.

- Tabela: `TB_AGENDA_MEDICA`
- Campos: `id`, `medico`, `diaSemana` (via `DiaSemanaConverter`), `horaInicio`, `horaFim`, `duracaoMinutos`, `status` (IndicativoStatus A/I), `dataCadastro`, `dataUltimaAtualizacao`
- Validações: horários obrigatórios, início antes do fim, duração maior que zero, faixa comporta ao menos um slot
- Métodos: `criar`, `validar`, `atualizar`, `ativar`, `inativar`, `isAtiva`

### 1.3 AgendaMedicaExcecao

Período de indisponibilidade do médico.

- Tabela: `TB_AGENDA_MEDICA_EXCECAO`
- Campos: `id`, `medico`, `dataInicio`, `dataFim`, `motivo`, `tipo` (`TipoExcecaoAgenda`), `dataCadastro`
- Validações: médico e datas obrigatórios, início antes do fim, tipo obrigatório
- CHECKs no banco: `dt_fim > dt_inicio`, `tp_excecao IN ('FERIAS','BLOQUEIO','EVENTO')`

### 1.4 AgendaMedicaSlot

Horário concreto materializado.

- Tabela: `TB_AGENDA_MEDICA_SLOT`
- Campos: `id`, `medico`, `especialidade` (opcional), `dataInicio`, `dataFim`, `status` (`StatusSlotAgenda`), `idConsulta` (Long puro, sem mapeamento ManyToOne para evitar ciclo), `dataCadastro`, `dataUltimaAtualizacao`
- Restrição única: (`id_medico`, `dt_inicio`)
- Métodos: `criar`, `reservar(idConsulta)`, `liberar`, `bloquear`, `cancelar`, `isLivre`
- Regras de transição: reserva exige LIVRE; bloqueio não permitido em RESERVADO

### 1.5 Consulta

Reserva de um slot por um paciente, com snapshot financeiro.

- Tabela: `TB_CONSULTA`
- Campos: `id`, `paciente`, `medico`, `especialidade`, `slot`, `pacienteConvenio` (nulo em particular), `tipoAtendimento` (`TipoAtendimentoConsulta`), `status` (`StatusConsulta`), `dataConsulta`, `dataCancelamento`, `motivoCancelamento`, `valorConsulta`, `valorCopagamento`, `valorCoberturaConvenio`, `dataCadastro`, `dataUltimaAtualizacao`
- Restrição única: `id_agenda_slot` (anti-overbooking)
- Validações: paciente, médico, slot e tipo obrigatórios; convênio exige vínculo paciente-convênio
- Transições: `confirmar`, `marcarRealizada`, `marcarFalta`, `cancelar(motivo)`

## 2. Enums

| Enum | Valores | Observações |
|------|---------|-------------|
| `DiaSemana` | SEGUNDA(1) .. DOMINGO(7) | Possui descricao, abreviacao, `toDayOfWeek`, `fromValor`, `fromDayOfWeek` |
| `StatusConsulta` | AGENDADA, CONFIRMADA, REALIZADA, CANCELADA, FALTOU | |
| `StatusSlotAgenda` | LIVRE, RESERVADO, BLOQUEADO, CANCELADO | |
| `TipoAtendimentoConsulta` | CONVENIO, PARTICULAR | |
| `TipoExcecaoAgenda` | FERIAS, BLOQUEIO, EVENTO | |

## 3. Conversor

- `DiaSemanaConverter`: `AttributeConverter<DiaSemana, Integer>`. Persiste 1..7. Não é auto-aplicado; declarado por campo via `@Convert`.

## 4. Campo adicional em Medico

- `Medico.valorConsulta` (`NR_VALOR_CONSULTA`, NUMERIC(10,2)): valor base de consulta particular. Quando nulo, o serviço usa o valor padrão de 250,00.

## 5. Migrations

| Migration | Conteúdo |
|-----------|----------|
| V21 | Cria `TB_MEDICO_ESPECIALIDADE`; migra o vínculo 1:1 atual de `tb_medico.id_especialidade` como principal; adiciona `nr_duracao_consulta_min` em `tb_medico` e `nr_duracao_padrao_min` (default 30) em `tb_especialidade`. |
| V22 | Cria `TB_AGENDA_MEDICA` com CHECKs (`nr_dia_semana` entre 1 e 7, `hr_fim > hr_inicio`, `nr_duracao_min > 0`). |
| V23 | Cria `TB_AGENDA_MEDICA_EXCECAO` com CHECK de tipo. |
| V24 | Cria `TB_AGENDA_MEDICA_SLOT` com UNIQUE (`id_medico`, `dt_inicio`) e CHECK de status. FK para `tb_consulta` não criada aqui (ciclo). |
| V25 | Cria `TB_CONSULTA` com UNIQUE (`id_agenda_slot`), FKs para paciente, médico, especialidade, slot e paciente-convênio; CHECKs de tipo e status. |
| V26 | Adiciona FK `id_consulta -> tb_consulta` em `tb_agenda_medica_slot`; índices compostos para consultas e slots. |
| V27 | Adiciona `nr_valor_consulta` em `tb_medico` e define valor padrão para registros existentes. |

## 6. Convenções de mapeamento

- `AgendaMedicaSlot.idConsulta` é um `Long` puro (sem `@ManyToOne`) para evitar ciclo de mapeamento com `Consulta`. A FK no banco é criada na V26, após `TB_CONSULTA` existir.
- `Consulta` possui `@ManyToOne` para `AgendaMedicaSlot` e para `PacienteConvenio` (nulo em particular).
- Status simples (faixa, regra) usam `IndicativoStatus` (A/I) com `IndicativoStatusConverter`.
- Status de slot e consulta usam `@Enumerated(STRING)`.
- Todas as entidades novas estão registradas em `hsg-his-service/src/main/resources/META-INF/persistence.xml`.

## 7. Dados de teste (seed DEV)

O seed em `infra/db/seed/init.sql` popula, para os médicos demo:

- Grades semanais variadas (manhãs, tardes, sábados) e uma faixa inativa histórica.
- Especialidades secundárias (vínculos N:N adicionais).
- Exceções cobrindo passado, presente e futuro nos três tipos.
- Valor de consulta por médico.
- Convênios ativos para `claudio.filho` (adesão há 200 dias) e `mariana.santos` (adesão há 10 dias).
- Slots LIVRE materializados para os próximos 21 dias (replicando a geração via SQL, respeitando exceções e idempotência).
- Consultas demonstrando os estados AGENDADA, CONFIRMADA, REALIZADA, CANCELADA e atendimento por convênio.

O seed só executa em banco limpo. Para recarregar: `docker-compose down -v && docker-compose up --build`.
