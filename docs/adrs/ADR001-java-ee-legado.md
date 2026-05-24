# ADR-001: Arquitetura do Sistema Hospitalar - Monólito Modular com EAR (Java EE Legado)

## Status

Aprovada

---

## Context

O sistema de Gestão Hospitalar (HSG HIS) será desenvolvido como um MVP com foco em simular um ambiente corporativo real baseado em tecnologias legadas amplamente utilizadas em grandes instituições de saúde.

O contexto do domínio inclui:

- Fluxo de atendimento hospitalar (recepção → consulta → atendimento)
- Cadastro e gestão de pacientes
- Agendamento de consultas médicas
- Registro de atendimentos clínicos
- Controle básico de usuários por perfil

O ambiente operacional do sistema exige:

- Consistência transacional forte
- Processamento centralizado
- Baixa complexidade operacional inicial
- Integração com servidor de aplicação corporativo
- Simulação de arquitetura encontrada em sistemas legados reais

Foram consideradas as seguintes abordagens arquiteturais:

- Monólito modular com Java EE (EAR)
- Microsserviços com Spring Boot

---

## Decision

Será adotada uma arquitetura de **monólito modular empacotado em EAR utilizando Java EE (Jakarta EE legado)**.

O sistema será estruturado em módulos Maven bem definidos, executando dentro de um único servidor de aplicação, com separação lógica de responsabilidades.

A estrutura será baseada em:

- JSF para camada de apresentação
- EJB para regras de negócio
- JPA para persistência
- WildFly como servidor de aplicação
- Empacotamento EAR como unidade única de deploy

Separação lógica dos módulos:

- `hsg-his-domain` → Entidades e modelo de domínio
- `hsg-his-service` → Regras de negócio (EJB)
- `hsg-his-web` → Camada de apresentação (JSF + PrimeFaces)
- `hsg-his-ear` → Empacotamento e distribuição

---

## Justificativa

### 1. Aderência a ambientes corporativos legados

Sistemas hospitalares reais frequentemente utilizam Java EE com:

- EJB para lógica de negócio
- JSF para interface web server-side
- Servidores de aplicação como WildFly

**Impacto:**

- Simulação realista de ambientes enterprise
- Alinhamento com sistemas existentes no mercado hospitalar

---

### 2. Consistência transacional forte

O domínio hospitalar exige consistência absoluta em operações críticas:

- Registro de atendimento
- Agendamento de consultas
- Atualização de prontuário
- Alterações de status clínico

**Java EE com EJB + JPA:**

- Transações gerenciadas pelo container (JTA)
- Garantia de atomicidade (ACID)
- Menor risco de inconsistência de dados

---

### 3. Baixa complexidade operacional inicial

Comparado a microsserviços:

- Não há necessidade de orquestração
- Não há comunicação distribuída
- Não há gerenciamento de múltiplos deploys

**Impacto:**

- Redução significativa da complexidade de infraestrutura
- Maior foco no domínio de negócio

---

### 4. Forte coesão do domínio hospitalar

O domínio apresenta alta interdependência:

- Paciente está ligado a consultas
- Consultas geram atendimentos
- Atendimentos dependem de médicos e agenda

**Impacto:**

- Separação em serviços independentes traria complexidade artificial
- Monólito modular mantém coesão e consistência

---

### 5. Modelo de execução simplificado

O sistema será executado como um único artefato:

- Um único deploy (EAR)
- Um único runtime (WildFly)
- Compartilhamento de contexto entre módulos

**Impacto:**

- Menor overhead operacional
- Facilidade de debug e deploy

---

### 6. Evolução futura possível

Apesar de monólito, a estrutura modular permite evolução controlada:

- Separação clara por responsabilidade
- Módulos independentes no nível de código
- Possibilidade de extração futura de serviços

---

## Alternatives Considered

### Microsserviços

**Prós:**

- Escalabilidade independente por domínio
- Deploy desacoplado
- Isolamento de falhas

**Contras:**

- Alta complexidade operacional
- Necessidade de comunicação distribuída (REST/messaging)
- Problemas de consistência eventual
- Overhead de infraestrutura incompatível com o MVP

---

### Monólito em WAR único (sem modularização)

**Prós:**

- Simplicidade máxima
- Menor configuração inicial

**Contras:**

- Baixa escalabilidade interna
- Alto acoplamento de código
- Dificuldade de manutenção futura

---

## Consequences

### Positivas

- Alta consistência transacional
- Simplicidade operacional
- Alinhamento com sistemas legados reais
- Facilidade de desenvolvimento inicial
- Boa separação lógica via módulos Maven
- Deploy único (EAR)

---

### Negativas

- Escalabilidade vertical limitada
- Deploy único para todo o sistema
- Maior dependência do servidor de aplicação
- Menor flexibilidade arquitetural comparado a stacks modernas

---

## Evolution Strategy

A arquitetura foi desenhada para evolução progressiva.

A modularização permite:

- Separação clara de responsabilidades
- Identificação de bounded contexts
- Possível extração futura de módulos

### Critérios para evolução futura:

- Aumento de carga em módulos específicos (ex: agendamento)
- Necessidade de escalabilidade independente
- Adoção de integrações externas mais complexas
- Evolução para arquitetura distribuída

### Estratégia de evolução:

- Extração gradual de módulos do EAR
- Introdução de APIs REST para comunicação externa
- Possível migração para microsserviços por domínio
- Substituição progressiva do JSF por frontend desacoplado (se necessário)

---

## Scope

Esta decisão se aplica a:

- Toda a arquitetura do backend do HSG HIS
- Estrutura de módulos Maven
- Estratégia de deploy
- Modelo de execução no WildFly

---

## References

- Jakarta EE / Java EE Architecture Guide
- EJB Specification
- JSF Component-Based UI Model
- WildFly Application Server Documentation
- Strangler Pattern (evolução arquitetural)
- Domain-Driven Design (DDD)