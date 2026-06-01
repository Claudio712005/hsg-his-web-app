# Tela: Agendar Consulta (Paciente)

Caminho: `paciente/agendar-consulta.xhtml`
Bean: `ConsultaBuscaBean` (`@ViewScoped`, `@Named("consultaBuscaBean")`)
Perfil: paciente

## 1. Objetivo

Permitir que o paciente busque horários livres por especialidade, médico (opcional) e data, e agende uma consulta, escolhendo entre convênio e particular quando aplicável.

## 2. Estrutura da tela

### 2.1 Banner de convênio

- Se houver convênio ativo: exibe convênio e plano e informa que o agendamento pode ser por convênio (sujeito a carência e copagamento) ou particular.
- Sem convênio ativo: informa que as consultas serão particulares.

### 2.2 Filtros de busca

- Especialidade (obrigatória). Ao mudar, recarrega a lista de médicos da especialidade e limpa os resultados.
- Médico (opcional). Habilita após a especialidade ter médicos vinculados; permite "Qualquer médico".
- Data (obrigatória).
- Botão "Buscar".

### 2.3 Resultados

- Grade de cartões de horário livre, cada um com a hora, o nome do médico e o botão "Agendar".
- Mensagem informativa quando não há horários para os filtros.

### 2.4 Diálogo de confirmação

- Resumo: especialidade, médico e data/hora.
- Quando há convênio ativo: caixa "Usar convênio" que recalcula o financeiro ao alternar.
- Resumo financeiro: tipo de atendimento, valor da consulta, cobertura do convênio (se aplicável) e valor a pagar.
- Aviso quando o procedimento está em carência (o cálculo cai para particular).
- Ações: Cancelar e Confirmar.

## 3. Fluxo

1. Selecionar especialidade; opcionalmente médico; informar a data.
2. Buscar horários livres.
3. Clicar em "Agendar" no horário desejado.
4. Conferir o resumo e o financeiro; ajustar "Usar convênio" se desejado.
5. Confirmar. Em caso de sucesso, a lista de horários é atualizada.

## 4. Cálculo financeiro (preview)

- A simulação é tolerante: se o convênio estiver indisponível ou em carência, mostra o resultado particular com aviso explicativo.
- O agendamento efetivo é estrito: se o convênio for solicitado e estiver indisponível ou em carência, a operação é recusada com orientação para agendar como particular.

Detalhes em `docs/regras/regras-negocio-agendamento.md` (seções 4, 5 e 6).

## 5. Validações e mensagens

- Mensagens via `p:growl`.
- Bloqueios com retorno ao usuário: especialidade ausente, data ausente, ausência de horário selecionado.
- Mensagens do serviço/domínio refletidas mesmo quando encapsuladas por `EJBException` (ex.: horário não disponível, antecedência insuficiente, limite de consultas futuras, convênio em carência).

## 6. Observações de implementação

- A confirmação usa lock pessimista no slot no momento do agendamento, evitando double booking.
- A lista exibida é recarregada após o agendamento para refletir o slot que deixou de estar livre.
- Ícones em Font Awesome 5.
