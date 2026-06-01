# Módulo de Notificações

Notificações internas (in-app) entregues aos quatro perfis autenticados do sistema: paciente, médico, enfermeiro e administrador. Trabalham em conjunto com os e-mails já existentes, oferecendo uma trilha persistente dentro do portal.

## 1. Princípios

- Notificações são persistidas no banco e ficam disponíveis ao destinatário por **40 dias**, contados a partir da criação. Após esse prazo são removidas em definitivo por um job agendado.
- Cada notificação pertence a um único destinatário (perfil + id). Mensagens para vários destinatários geram vários registros (um por destinatário).
- A camada de envio in-app não interrompe a operação de negócio: falhas no envio são apenas registradas em log.

## 2. Modelo de dados

Tabela `TB_NOTIFICACAO` (migration **V28**):

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id_notificacao` | BIGINT | PK |
| `tp_destinatario` | VARCHAR(15) | `PACIENTE`, `MEDICO`, `ENFERMEIRO`, `ADMIN` |
| `id_destinatario` | BIGINT | id na tabela do perfil (sem FK; consistência aplicacional) |
| `ds_titulo` | VARCHAR(200) | título curto |
| `ds_mensagem` | VARCHAR(1000) | corpo da notificação |
| `ds_link` | VARCHAR(500) | link interno opcional (relativo ao contexto) |
| `tp_notificacao` | VARCHAR(12) | `INFO`, `SUCESSO`, `ALERTA`, `ERRO` |
| `tp_categoria` | VARCHAR(12) | `CONSULTA`, `CONVENIO`, `AGENDA`, `SISTEMA` |
| `fl_lida` | CHAR(1) | `S` / `N` |
| `dt_leitura` | TIMESTAMP | preenchido quando marcada como lida |
| `dt_criacao` | TIMESTAMP | |
| `dt_expiracao` | TIMESTAMP | `dt_criacao + 40 dias` |

Índices: `(tp_destinatario, id_destinatario, fl_lida)`, `dt_expiracao`, `dt_criacao DESC`.

Entidade JPA: `Notificacao` (`hsg-his-domain`). Factory `Notificacao.criar(...)` aplica validações e calcula a expiração; método `marcarComoLida()` é idempotente.

Enums: `TipoDestinatarioNotificacao`, `TipoNotificacao`, `CategoriaNotificacao`.

## 3. Camadas

- DAO: `NotificacaoDAO` (`hsg-his-service`) — `listarPorDestinatario`, `contarNaoLidas`, `marcarTodasComoLidas`, `removerExpiradas`, `salvar`, `atualizar`.
- Facade: `NotificacaoServiceFacade` + `NotificacaoServiceImpl`.
- Bean web: `NotificacoesBean` (`@ViewScoped @Named`) — descobre perfil/id via `BeanSessao`.
- Fragmento de UI: `WEB-INF/templates/fragments/notificacoes-corpo.xhtml`.
- Telas por perfil:
  - `paciente/notificacoes.xhtml`
  - `admin/notificacoes.xhtml`
  - `clinica/notificacoes.xhtml` (atende médico e enfermeiro, perfis que compartilham o layout clínico)

## 4. Tela de notificações

A mesma estrutura aparece para os quatro perfis (via inclusão do fragmento):

- Cabeçalho com contagem de não lidas e botão "Marcar todas como lidas" (desabilitado quando o contador está em zero).
- Barra de filtros com seleção entre "Todas", "Não lidas" e "Lidas", além de campo de busca por título ou mensagem (case-insensitive). A busca é aplicada via ajax com debounce; botão "Limpar" só aparece quando há filtro ativo.
- Cartões individuais por notificação: ícone colorido por tipo, título, mensagem (preserva quebras de linha), categoria, data de criação e indicador de leitura. Cartões não lidos têm destaque visual (fundo azul claro e um marcador no canto superior direito).
- Botão "Abrir" quando há link associado e botão de marcar como lida quando ainda não foi.
- Listagem paginada do lado servidor (limite padrão 100). Notificações mais recentes em primeiro.
- Mensagem vazia adapta-se ao contexto ("Você não possui notificações no momento." quando sem filtro; "Nenhuma notificação encontrada para os filtros aplicados." quando há filtro/busca ativos).

## 4.1 Alerta visual (sino)

Sino global no header de cada layout (`paciente`, `admin`, `clinica`) sinaliza notificações não lidas em tempo quase-real:

- Componente: `SinoNotificacoesBean` (`@RequestScoped`) descobre o perfil/id via `BeanSessao`, expõe `contagem`, `temNaoLidas`, `linkNotificacoes`.
- Fragmento: `WEB-INF/templates/fragments/sino-notificacoes.xhtml` — ícone de sino + badge vermelho com contagem (mostra `99+` para valores acima de 99) e animação CSS de pulso.
- Polling: `p:poll interval="30"` dentro do próprio fragmento atualiza o badge a cada 30 segundos sem reload da página. Como o bean é `@RequestScoped`, cada ciclo de poll dispara uma consulta fresca ao `contarNaoLidas`.
- Refresh por troca de tela: navegação para qualquer página re-renderiza o header, atualizando a contagem.
- Cliques no sino vão para a tela de notificações do perfil correspondente.

A combinação de polling de 30 s e refresh na troca de página entrega percepção quase imediata sem custo significativo de banco (uma `COUNT` por usuário ativo a cada 30 s).

## 5. Job de limpeza

`NotificacaoLimpezaTimer` (`@Singleton @Startup`) com `@Schedule(hour="3", minute="0", second="0", persistent=false, info="notificacao-limpeza")` executa diariamente às **03:00** e chama `NotificacaoServiceImpl.limparExpiradas()`, que executa `DELETE FROM Notificacao n WHERE n.dataExpiracao < :agora`.

Detalhes operacionais e configuração de horários estão em `agendamento-schedules.md` (job de auto-falta) e seguem o mesmo padrão de timer EJB do projeto.

## 6. Ações do sistema que geram notificações

Mapeamento atual das ações que disparam notificações in-app. As notificações in-app coexistem com os e-mails já configurados, sem substituí-los.

| Ação | Disparador | Destinatário | Tipo | Categoria | Link |
|------|------------|--------------|------|-----------|------|
| Agendar consulta | `ConsultaServiceImpl.agendar` | Paciente | SUCESSO | CONSULTA | `/paciente/minhas-consultas.xhtml` |
| Agendar consulta | `ConsultaServiceImpl.agendar` | Médico | INFO | CONSULTA | `/clinica/notificacoes.xhtml` |
| Cancelar consulta (paciente) | `ConsultaServiceImpl.cancelarPeloPaciente` | Médico | ALERTA | CONSULTA | `/clinica/notificacoes.xhtml` |
| Auto-falta | `ConsultaAutoFaltaServiceImpl.marcarFaltasAutomaticas` | Médico | ALERTA | CONSULTA | `/clinica/notificacoes.xhtml` |
| Auto-falta | `ConsultaAutoFaltaServiceImpl.marcarFaltasAutomaticas` | Paciente | INFO | CONSULTA | `/paciente/minhas-consultas.xhtml` |
| Solicitar adesão a convênio | `ConvenioPacienteServiceImpl.solicitarConvenio` | Todos os admins ativos | INFO | CONVENIO | `/admin/aprovacao-convenios.xhtml` |
| Aprovar convênio | `AprovacaoConvenioServiceImpl.aprovar` | Paciente | SUCESSO | CONVENIO | `/paciente/meu-convenio.xhtml` |
| Rejeitar convênio | `AprovacaoConvenioServiceImpl.rejeitar` | Paciente | ALERTA | CONVENIO | `/paciente/meu-convenio.xhtml` |
| Cancelar solicitação de convênio (paciente) | `ConvenioPacienteServiceImpl.cancelarSolicitacao` | Todos os admins ativos | INFO | CONVENIO | `/admin/aprovacao-convenios.xhtml` |
| Informar alergia | `AlergiaServiceImpl.informarAlergia` | Paciente | INFO | SISTEMA | — |
| Informar alergia | `AlergiaServiceImpl.informarAlergia` | Todos os admins ativos | ALERTA | SISTEMA | — |
| Aprovar alergia | `AlergiaServiceImpl.aprovarAlergia` | Paciente | SUCESSO | SISTEMA | — |
| Rejeitar alergia | `AlergiaServiceImpl.rejeitarAlergia` | Paciente | ALERTA | SISTEMA | — |
| Excluir alergia | `AlergiaServiceImpl.excluirAlergia` | Paciente | INFO | SISTEMA | — |
| Solicitar atualização cadastral/endereço/clínica | `SolicitacaoAtualizacaoServiceImpl.solicitar*` | Todos os admins ativos | INFO | SISTEMA | — |
| Cancelar solicitação de atualização (admin/clínica) | `SolicitacaoAtualizacaoServiceImpl.cancelarSolicitacao` | Paciente | ALERTA | SISTEMA | — |
| Ativação de profissional (conclusão de cadastro) | `AtivacaoServiceImpl.ativarCadastro` | Todos os admins ativos | SUCESSO | SISTEMA | `/admin/pre-cadastro-profissional.xhtml` |

Pontos a observar:

- Para a ação de solicitação de adesão, a notificação é endereçada a cada administrador ativo (`AdminDAO.listarIdsAtivos`). Em ambientes com muitos administradores ativos, considerar fan-out assíncrono futuramente.
- Falhas no envio (ex.: NotificacaoService indisponível em testes ou em momento de transação parcial) são capturadas em logs com nível WARNING; a ação principal não é desfeita.

## 7. Regras do destinatário

- Só pode marcar como lida a notificação que pertence ao próprio destinatário; o service rejeita tentativas em outros registros com `IllegalStateException`.
- "Marcar todas como lidas" atua somente sobre as notificações do perfil/id da sessão e atualiza `dt_leitura` para o instante atual.
- Notificações lidas continuam visíveis na lista até a expiração para fins de auditoria do destinatário; após 40 dias são removidas pelo job.

## 8. E-mails relacionados

A camada de e-mail (`MailService`) continua sendo o canal externo. Para a ação de auto-falta, o template HTML foi reformulado e cobre:

- Cabeçalho com identificação do sistema.
- Selo "Aviso automático".
- Caixa com paciente e data/hora da consulta.
- Botão chamado "Abrir portal HSG HIS" apontando para `clinica/notificacoes.xhtml`.
- Rodapé indicando que se trata de e-mail automático.

Em DEV os e-mails caem no MailHog (porta 8025) e podem ser inspecionados visualmente para validar o layout.

## 9. Testes

- Domínio: `NotificacaoTest` — fábrica, validações, idempotência de leitura, expiração de 40 dias.
- Serviço: `NotificacaoServiceImplTest` — caso feliz, validações de parâmetros, defesa de posse na leitura, limpeza de expiradas.
- Wiring nas ações existentes: cobertos indiretamente pelos testes de `ConsultaServiceImplTest` e `ConsultaAutoFaltaServiceImplTest`, que continuam verdes (a notificação é opcional: falhas são engolidas para não desfazer a operação principal).
