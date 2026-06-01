# Tela: Minhas Consultas (Paciente)

Caminho: `paciente/minhas-consultas.xhtml`
Bean: `MinhasConsultasBean` (`@ViewScoped`, `@Named("minhasConsultasBean")`)
Perfil: paciente

## 1. Objetivo

Listar as consultas do paciente, exibir detalhes e permitir o cancelamento dentro das regras de antecedência.

## 2. Estrutura da tela

### 2.1 Tabela de consultas

Colunas: data/hora, médico, especialidade, tipo de atendimento, valor a pagar, status e ações.

- Status exibido como chip com cor por situação (agendada, confirmada/realizada, cancelada/faltou).
- Ações por linha: "Ver detalhes" (sempre) e "Cancelar" (apenas para AGENDADA ou CONFIRMADA).

### 2.2 Diálogo de detalhes

Exibe status, médico, especialidade, data/hora, tipo de atendimento, valor da consulta, cobertura do convênio (apenas se atendimento por convênio), valor a pagar e motivo do cancelamento (apenas se cancelada).

### 2.3 Diálogo de cancelamento

- Informa a regra de antecedência (24h).
- Campo de motivo (obrigatório).
- Ações: Voltar e Confirmar cancelamento.

## 3. Fluxo de cancelamento

1. Clicar em "Cancelar" na consulta desejada.
2. Informar o motivo.
3. Confirmar. O sistema valida posse e antecedência, cancela a consulta e libera o slot.

## 4. Validações e mensagens

- Mensagens via `p:growl`.
- Bloqueios com retorno ao usuário: consulta não selecionada, motivo ausente.
- Mensagens do serviço/domínio refletidas (ex.: cancelamento com menos de 24h, consulta de outro paciente, consulta já finalizada).

Detalhes em `docs/regras/regras-negocio-agendamento.md` (seção 7).

## 5. Observações de implementação

- A listagem usa carregamento com associações necessárias (médico, especialidade e slot) já buscadas, evitando inicialização tardia na tela.
- O vínculo de convênio da consulta não é acessado na tela de detalhes para evitar inicialização tardia; os valores financeiros vêm do snapshot da própria consulta.
- Liberação do slot no cancelamento ocorre sob lock pessimista.
