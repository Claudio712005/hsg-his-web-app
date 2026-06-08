# Tela: Agenda Médica (Administração)

Caminho: `admin/agenda-medica.xhtml`
Bean: `AgendaMedicaBean` (`@ViewScoped`, `@Named("agendaMedicaBean")`)
Perfil: administração/recepção (role `clinica`/`admin`)

## 1. Objetivo

Configurar a disponibilidade dos médicos: cadastrar a grade semanal, registrar exceções e gerar os horários (slots) que ficarão disponíveis para agendamento.

## 2. Estrutura da tela

### 2.1 Cabeçalho e conceitos

- Título e subtítulo com a finalidade da tela.
- Barra de conceitos (Faixa -> Slots -> Consultas) com exemplos.
- Botão "Como usar" abre diálogo de tutorial em 4 passos.

### 2.2 Barra de ferramentas (sticky)

Card único contendo:

- Seletor de médico com filtro por nome, CRM ou especialidade. O item exibe nome, CRM e especialidade.
- Campo "Dias a gerar" (1 a 180).
- Botão "Gerar slots" (médico selecionado).
- Botão "Em massa..." (abre diálogo de geração para vários médicos).
- Faixa de chips com informações do médico selecionado (CRM, especialidade, número de faixas, exceções e slots livres futuros).

Toda a página (abas de grade, exceções e lista) reflete o médico selecionado. As abas de calendário não dependem dessa seleção.

### 2.3 KPIs

Quatro cartões: faixas na grade, exceções registradas, slots livres futuros e especialidades vinculadas.

### 2.4 Abas

1. Calendário geral: visão consolidada de todos os médicos ativos. Cor de borda por médico (legenda) e cor de fundo por status do evento. Somente leitura.
2. Calendário por médicos: seleção múltipla de médicos para comparar agendas. Atalhos "Todos" e "Limpar"; o calendário atualiza ao marcar/desmarcar.
3. Grade semanal: banner do médico selecionado, explicação de faixa, grade visual de 7 colunas (uma por dia) e formulário em acordeão para cadastrar/editar faixa.
4. Exceções: banner do médico, tabela de exceções e formulário de nova exceção.
5. Lista de slots: banner do médico, tabela paginada dos slots dos próximos dias.

## 3. Funcionalidades por aba

### 3.1 Grade semanal

- Cada faixa aparece na coluna do seu dia com horário e ações.
- Ações por faixa: editar, inativar (com confirmação), reativar.
- A faixa em edição é destacada visualmente.
- Formulário (acordeão): dia da semana, duração do slot, horário de início e fim. O dia não é editável ao alterar uma faixa existente.
- Após salvar, é necessário gerar slots para materializar os horários.

### 3.2 Exceções

- Tabela com tipo, início, fim e motivo; ação de remover (com confirmação).
- Formulário: tipo (FERIAS, BLOQUEIO, EVENTO), início, fim e motivo (opcional).

### 3.3 Geração de slots

- Individual: usa o médico selecionado e o campo "Dias a gerar".
- Em massa: diálogo com seleção múltipla de médicos, atalhos "Selecionar todos" e "Limpar seleção", período em dias e resumo dinâmico. Resultado agregado informa total de slots e médicos; falhas isoladas não interrompem os demais.
- A geração é idempotente.

### 3.4 Detalhe de evento (calendários)

- Clique em um evento (slot ou exceção) abre diálogo com detalhes: tipo, médico, status, datas, especialidade ou motivo.

## 4. Validações e mensagens

- Mensagens exibidas via `p:growl` (canto da tela, com detalhe).
- As mensagens refletem a regra violada (a mensagem real do serviço/domínio é extraída mesmo quando encapsulada por `EJBException`).
- Exemplos: sobreposição de faixa, sobreposição de exceção, faixa menor que a duração, ausência de médico selecionado, período de geração fora do intervalo permitido.

Regras detalhadas em `docs/regras/regras-negocio-agendamento.md` (seções 1, 2 e 3).

## 5. Observações de implementação

- Ícones usam Font Awesome 5 (classes no padrão da versão 5).
- Tooltips e textos de ajuda foram simplificados para reduzir poluição visual; orientações ficam no tutorial e em textos de apoio dos formulários.
- Calendário (`p:schedule`) dimensiona a altura conforme o conteúdo e permanece dentro do painel.
