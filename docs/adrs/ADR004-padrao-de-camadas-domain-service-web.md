# ADR-004: Arquitetura em Camadas do Sistema HSG HIS

## Status

Aprovada

---

## Context

O sistema HSG HIS será desenvolvido utilizando Java EE tradicional, executando em :contentReference[oaicite:0]{index=0}, com foco em um cenário hospitalar corporativo.

O domínio do sistema inclui processos críticos como:

- Cadastro e gestão de pacientes
- Agendamento de consultas
- Atendimento médico
- Registro de informações clínicas
- Controle de usuários por perfis

Dado esse contexto, é necessário definir uma separação clara de responsabilidades dentro da aplicação para garantir:

- Manutenibilidade
- Organização do código
- Baixo acoplamento lógico
- Consistência transacional
- Evolução controlada do sistema

Foram consideradas as seguintes abordagens:

- Arquitetura em camadas tradicional (Layered Architecture)
- Arquitetura hexagonal (Ports and Adapters)
- Arquitetura baseada em microservices

---

## Decision

Será adotada uma **Arquitetura em Camadas (Layered Architecture)** dentro de um modelo monolítico modular.

A aplicação será organizada em três camadas principais:

### 1. Camada de Apresentação (Web)

Responsável pela interface com o usuário:

- JSF (JavaServer Faces)
- PrimeFaces
- Managed Beans / Backing Beans

Responsabilidades:

- Renderização de telas
- Controle de navegação
- Validação básica de formulário
- Interação com camada de serviço

---

### 2. Camada de Aplicação / Serviço

Responsável pelas regras de negócio:

- EJB Stateless / Stateful
- Orquestração de casos de uso
- Validações de negócio
- Coordenação de transações

Responsabilidades:

- Implementar regras hospitalares
- Garantir consistência de operações
- Controlar fluxo de atendimento

---

### 3. Camada de Domínio / Persistência

Responsável pelo modelo de dados:

- Entidades JPA
- Mapeamento ORM
- Estrutura de dados do sistema

Responsabilidades:

- Representação do domínio hospitalar
- Persistência de dados
- Relacionamentos entre entidades

---

## Justificativa

### 1. Compatibilidade com Java EE

A arquitetura em camadas é nativamente suportada pelo ecossistema Java EE:

- JSF → camada de apresentação
- EJB → camada de serviço
- JPA → camada de persistência

**Impacto:**

- Integração natural entre componentes
- Menor necessidade de adaptações arquiteturais

---

### 2. Clareza de responsabilidades

Cada camada possui um papel bem definido:

- Web → interface com usuário
- Service → regras de negócio
- Domain → dados e persistência

**Impacto:**

- Redução de acoplamento lógico
- Melhor legibilidade do sistema
- Facilidade de manutenção

---

### 3. Adequação ao domínio hospitalar

O sistema possui fluxos fortemente estruturados e transacionais:

- Atendimento médico
- Agendamento de consultas
- Registro clínico

**Impacto:**

- Centralização da lógica de negócio
- Garantia de consistência operacional

---

### 4. Simplicidade arquitetural

Comparado a abordagens mais complexas:

- Não há necessidade de ports/adapters
- Não há sobrecarga de eventos ou mensageria
- Não há distribuição de serviços

**Impacto:**

- Redução de complexidade inicial
- Foco no domínio de negócio

---

### 5. Alinhamento com modelo monolítico modular

A arquitetura em camadas será aplicada dentro de módulos Maven:

- `hsg-his-web` → apresentação
- `hsg-his-service` → regras de negócio
- `hsg-his-domain` → modelo de domínio

---

## Alternatives Considered

### Arquitetura Hexagonal

**Prós:**

- Alto desacoplamento
- Facilidade de testes
- Independência de frameworks

**Contras:**

- Complexidade desnecessária para MVP
- Overengineering para contexto legado
- Maior curva de aprendizado

---

### Microsserviços

**Prós:**

- Escalabilidade independente
- Deploy separado por domínio

**Contras:**

- Alta complexidade operacional
- Problemas de consistência distribuída
- Não aderente ao objetivo de simulação de legado

---

## Consequences

### Positivas

- Estrutura clara e previsível
- Integração natural com Java EE
- Facilidade de desenvolvimento
- Baixa complexidade inicial
- Consistência transacional garantida

---

### Negativas

- Menor flexibilidade arquitetural
- Possível acoplamento entre camadas se mal utilizado
- Limitação para escalabilidade horizontal por camada

---

## Evolution Strategy

A arquitetura em camadas pode evoluir gradualmente para modelos mais sofisticados:

### Possíveis evoluções:

- Introdução de arquitetura hexagonal em módulos críticos
- Separação de serviços em APIs REST
- Extração de bounded contexts para microsserviços

### Estratégia:

- Manter camadas bem definidas desde o início
- Evitar lógica de negócio na camada web
- Centralizar regras no serviço (EJB)
- Preparar domínio para eventual isolamento

---

## Scope

Esta decisão se aplica a:

- Organização interna dos módulos Maven
- Estrutura de código do HSG HIS
- Implementação da lógica de negócio
- Integração com :contentReference[oaicite:1]{index=1}

---

## References

- Java EE Architecture Guide
- Martin Fowler - Layered Architecture
- Enterprise Application Architecture Patterns
- Clean Code Principles