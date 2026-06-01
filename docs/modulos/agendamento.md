# Módulo de Agendamento de Consultas

Documentação técnica e funcional do módulo de agendamento de consultas médicas do HSG HIS.

## 1. Propósito

Permitir que a recepção/administração configure a disponibilidade dos médicos (grade semanal, exceções e geração de horários) e que o paciente busque horários livres e agende consultas, com integração às regras de convênio (carência e copagamento).

## 2. Escopo por fase

O módulo foi construído em fases incrementais.

| Fase | Objetivo | Situação |
|------|----------|----------|
| A | Base estrutural: entidades, enums, conversores e migrations | Concluída |
| B | Gestão da agenda médica (admin): grade, exceções, geração de slots | Concluída |
| C | Busca de horários pelo paciente (somente leitura) | Concluída |
| D | Agendamento efetivo: reserva atômica, convênio, carência, copagamento, cancelamento | Concluída |
| E | Gestão clínica (recepção opera a agenda diária) | Pendente |
| F | Notificações automáticas (e-mail, lembretes) | Pendente |
| G | Relatórios e KPIs operacionais | Pendente |

## 3. Conceitos

- Faixa de atendimento (`AgendaMedica`): janela contínua de um dia da semana em que o médico atende (ex.: Segunda 08:00 às 12:00).
- Duração do slot: tempo de cada consulta dentro da faixa (ex.: 30 minutos).
- Slot (`AgendaMedicaSlot`): horário concreto materializado a partir da faixa (ex.: 08:00 às 08:30). É a unidade que o paciente reserva.
- Exceção (`AgendaMedicaExcecao`): período em que o médico não atende (férias, bloqueio, evento). Slots dentro do período não são gerados.
- Consulta (`Consulta`): reserva de um slot por um paciente, com snapshot financeiro.

Relação resumida: Faixa semanal define o padrão; a geração materializa slots; o paciente reserva um slot, criando uma consulta.

## 4. Arquitetura por camada

Segue o padrão do projeto: DAO (Stateless, JPQL) -> ServiceFacade (Local) + ServiceImpl (Stateless) -> Bean JSF (ViewScoped, Named) -> XHTML.

### 4.1 Domínio (`hsg-his-domain`)

Entidades: `MedicoEspecialidade`, `AgendaMedica`, `AgendaMedicaExcecao`, `AgendaMedicaSlot`, `Consulta`.
Enums: `DiaSemana`, `StatusConsulta`, `StatusSlotAgenda`, `TipoAtendimentoConsulta`, `TipoExcecaoAgenda`.
Conversor: `DiaSemanaConverter` (Integer 1..7 <-> DiaSemana).
Campo adicionado: `Medico.valorConsulta` (BigDecimal), origem do valor base da consulta.

Detalhes em `docs/dominio/modelo-agendamento.md`.

### 4.2 Serviço (`hsg-his-service`)

DAOs:
- `AgendaMedicaDAO`: grade por médico, ativas, `buscarSobreposicao`.
- `AgendaMedicaExcecaoDAO`: exceções por médico, vigentes no período, `buscarSobreposicao`.
- `AgendaMedicaSlotDAO`: slots por período, livres por especialidade/data, `buscarComLock` (lock pessimista), idempotência.
- `MedicoEspecialidadeDAO`: vínculos N:N, `listarMedicosPorEspecialidade`.
- `ConsultaDAO`: persistência, listagem por paciente, contagem de futuras ativas.

Facades e implementações:
- Admin: `AgendaMedicaServiceFacade` / `AgendaMedicaServiceImpl` (Fase B).
- Paciente: `ConsultaBuscaServiceFacade` / `ConsultaBuscaServiceImpl` (Fase C).
- Paciente: `ConsultaServiceFacade` / `ConsultaServiceImpl` (Fase D).

DTO: `ResultadoFinanceiroConsulta` (resultado do cálculo financeiro: tipo de atendimento, valores, flags de convênio e carência, observação).

### 4.3 Web (`hsg-his-web`)

Beans:
- `AgendaMedicaBean` (admin): grade, exceções, geração de slots (individual e em massa), calendários.
- `ConsultaBuscaBean` (paciente): busca de horários e confirmação de agendamento.
- `MinhasConsultasBean` (paciente): listagem, detalhes e cancelamento.

Telas:
- `admin/agenda-medica.xhtml`
- `paciente/agendar-consulta.xhtml`
- `paciente/minhas-consultas.xhtml`

Documentação por tela em `docs/telas/`.

## 5. Fluxos principais

### 5.1 Configuração da agenda (admin)

1. Selecionar o médico.
2. Cadastrar faixas semanais (dia, início, fim, duração do slot).
3. Opcionalmente registrar exceções.
4. Gerar slots para os próximos N dias (individual ou em massa para vários médicos).

### 5.2 Agendamento (paciente)

1. Selecionar especialidade, opcionalmente médico, e data.
2. Buscar horários livres.
3. Escolher um horário e confirmar.
4. Sistema calcula o financeiro (convênio ou particular), reserva o slot atomicamente e cria a consulta.

### 5.3 Cancelamento (paciente)

1. Acessar Minhas Consultas.
2. Cancelar uma consulta agendada/confirmada com 24h ou mais de antecedência, informando o motivo.
3. Sistema cancela a consulta e libera o slot.

## 6. Regras de negócio

Detalhadas em `docs/regras/regras-negocio-agendamento.md`. Resumo:

- Proibição de overbooking via restrição única no slot da consulta.
- Reserva atômica com lock pessimista no slot.
- Antecedência mínima de 2h e máxima de 90 dias para agendar.
- Limite de 3 consultas futuras ativas por paciente.
- Cancelamento pelo paciente apenas com 24h ou mais de antecedência.
- Carência por convênio bloqueia o atendimento por convênio; o particular permanece disponível.
- Snapshot financeiro persistido na consulta (valor, copagamento, cobertura do convênio).

## 7. Migrations

V21 a V27. Detalhes em `docs/dominio/modelo-agendamento.md`.

## 8. Testes

Cobertura por módulo (JUnit 4 + Mockito):

- Domínio: validações de `AgendaMedica`, `AgendaMedicaExcecao`, `AgendaMedicaSlot`, `MedicoEspecialidade`, `DiaSemanaConverter`.
- Serviço: `AgendaMedicaServiceImpl` (sobreposição de grade e exceção, geração de slots), `ConsultaServiceImpl` (lock, antecedência, limite, convênio, carência, copagamento, cancelamento), `ConsultaBuscaServiceImpl`.
- Web: `AgendaMedicaBean`, `ConsultaBuscaBean`, `MinhasConsultasBean` (guardas de validação, delegação ao serviço, tratamento de exceção).

DAOs não possuem teste unitário (JPQL puro; cobertos indiretamente pelos serviços e por validação em ambiente). 
