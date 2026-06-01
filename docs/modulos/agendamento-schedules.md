# Agendamento — Tarefas Agendadas (Schedules)

Tarefas automáticas (EJB Timer Service) executadas periodicamente pelo módulo de agendamento.

## 1. Auto-falta de consultas pendentes

### 1.1 Objetivo

Marcar como `FALTOU` consultas cujo horário previsto já passou e que permanecem com status `AGENDADA` ou `CONFIRMADA`. Cobre duas situações:

- Paciente não compareceu e ninguém registrou a falta.
- Atendimento ocorreu, mas o médico esqueceu de marcar como `REALIZADA`.

Em ambos os casos, o médico recebe um e-mail informando que a consulta foi marcada como falta automaticamente, com orientação para ajustar o status no portal se a consulta tiver sido realizada.

### 1.2 Componentes

- `ConsultaAutoFaltaTimer` (`@Singleton @Startup`): instala o EJB Timer e dispara o serviço de acordo com a expressão `@Schedule`.
- `ConsultaAutoFaltaServiceFacade` / `ConsultaAutoFaltaServiceImpl` (`@Stateless`): executa o processamento (idempotente por consulta, tolerante a falhas individuais).
- `ConsultaDAO.listarPendentesAteLimite(LocalDateTime limite)`: retorna consultas com `status IN (AGENDADA, CONFIRMADA)` e `dataConsulta < limite`.
- `MailService.enviarFaltaAutomaticaParaMedico(...)`: e-mail HTML enviado ao médico.

### 1.3 Configuração do timer

- Expressão: `@Schedule(hour="2", minute="0", second="0", persistent=false, info="consulta-auto-falta")`.
- Execução: uma vez por dia, às 02:00 (horário do servidor).
- Persistente: não. A definição vive em código; ao redeploy o timer é re-registrado.
- Acionamento avulso: invocação direta de `ConsultaAutoFaltaServiceFacade.marcarFaltasAutomaticas()` (útil para testes manuais ou para uma futura rotina manual).

### 1.4 Regra de tolerância

Constante `HORAS_TOLERANCIA = 24`. Uma consulta só é marcada como falta automaticamente se:

```
dataConsulta < (agora - 24 horas)
```

Isso dá ao médico até 24 horas após o horário previsto para registrar manualmente `REALIZADA`, `FALTOU` ou `CONFIRMADA -> REALIZADA` antes do sistema agir.

### 1.5 Fluxo de execução

1. Calcula `limite = agora - 24h`.
2. Consulta `listarPendentesAteLimite(limite)`.
3. Para cada consulta retornada:
   1. `consulta.marcarFalta()` (validação de domínio aplicada).
   2. Persiste via `consultaDAO.atualizar(consulta)`.
   3. Tenta enviar e-mail ao médico (falha de envio é registrada em log e não interrompe).
4. Log final agregando candidatas e processadas.

Falhas individuais (status já final, etc.) são capturadas em log de nível WARNING e não interrompem as demais.

### 1.6 E-mail ao médico

- Assunto: "Consulta marcada como falta automaticamente — HSG Hospital Information System".
- Corpo (HTML): saudação ao médico, dados da consulta (paciente, data/hora), instrução para corrigir manualmente se aplicável.
- Médico sem e-mail: a consulta ainda é marcada como falta; o envio é apenas pulado e registrado em log (WARNING).

### 1.7 Observações operacionais

- Sem dependência de coluna nova no banco; usa apenas `status`, `dataConsulta` e o e-mail do médico.
- O slot continua marcado como `RESERVADO` (consistente com o modelo: `FALTOU` mantém o slot reservado, evitando reuso do mesmo horário por outro paciente).
- Em ambiente DEV, o `MailService` envia para o MailHog (porta 8025) — útil para validar o conteúdo do e-mail.
- A janela de tolerância é uma constante de código. Caso evolua para configurável, mover para variável de ambiente ou parâmetro de sistema.

### 1.8 Testes

`ConsultaAutoFaltaServiceImplTest` cobre:

- Sem candidatas: nada é feito.
- Caso feliz: marca falta, persiste e envia e-mail com os dados esperados.
- Médico sem e-mail: marca falta sem tentar enviar.
- Falha em uma consulta: não interrompe as demais; somente as bem-sucedidas são contabilizadas.
