# ADR-002: Modelo de Empacotamento e Deploy - EAR como Unidade Principal

## Status

Aprovada

---

## Context

O sistema HSG HIS será executado em um ambiente corporativo baseado em servidor de aplicação Java EE, utilizando :contentReference[oaicite:0]{index=0} como runtime principal.

O sistema é composto por múltiplos módulos Maven:

- Camada de domínio (entidades)
- Camada de serviços (EJB)
- Camada web (JSF + PrimeFaces)
- Módulo de empacotamento (EAR)

O desafio arquitetural consiste em definir como esses módulos serão empacotados e distribuídos em ambiente de execução.

Foram consideradas as seguintes abordagens:

- Deploy único via EAR
- Deploy separado por módulo (WAR + EJB JAR independentes)
- Deploy único via WAR monolítico

---

## Decision

Será adotado o modelo de **empacotamento EAR (Enterprise Archive)** como unidade única de deploy do sistema.

O artefato final conterá:

- WAR (hsg-his-web) → camada de apresentação
- EJB JAR (hsg-his-service) → regras de negócio
- Domain JAR (hsg-his-domain) → modelo de domínio

O deploy será realizado como uma única unidade no servidor de aplicação.

---

## Justificativa

### 1. Aderência ao modelo Java EE tradicional

O modelo EAR é o padrão de referência em aplicações Java EE corporativas.

**Impacto:**

- Compatibilidade nativa com WildFly
- Suporte completo a EJB e JTA (Java Transaction API)
- Integração automática entre módulos

---

### 2. Integração simplificada entre camadas

Com EAR:

- EJBs são automaticamente expostos ao WAR
- CDI/EJB context é compartilhado
- Não há necessidade de configuração de rede ou REST interno

**Impacto:**

- Comunicação interna de baixa latência
- Menor complexidade de integração

---

### 3. Consistência transacional

Ao manter todos os módulos dentro do mesmo deploy:

- Transações distribuídas não são necessárias
- JTA gerencia transações de forma centralizada
- Evita problemas de consistência entre serviços

**Impacto:**

- Maior confiabilidade em operações hospitalares críticas
- Menor risco de inconsistência de dados

---

### 4. Redução de complexidade operacional

Comparado a múltiplos deploys:

- Apenas um artefato é versionado
- Apenas um deploy por release
- Menor necessidade de orquestração

**Impacto:**

- Simplicidade em ambientes de desenvolvimento e homologação
- Facilidade de manutenção inicial

---

### 5. Simulação de sistemas legados reais

Sistemas hospitalares e corporativos legados frequentemente utilizam:

- EAR em servidores de aplicação
- Forte acoplamento interno controlado
- Deploy único por release

**Impacto:**

- Realismo arquitetural para fins de estudo e simulação
- Aderência ao cenário enterprise tradicional

---

## Alternatives Considered

### Deploy separado por módulos (WAR + EJB independentes)

**Prós:**

- Maior desacoplamento físico
- Possibilidade de versionamento independente

**Contras:**

- Complexidade de integração via rede ou JNDI remoto
- Necessidade de gerenciamento de serviços separados
- Overhead operacional desnecessário para o MVP

---

### Monólito único em WAR

**Prós:**

- Simplicidade máxima
- Menor configuração inicial

**Contras:**

- Perda da separação entre camadas
- Acoplamento estrutural elevado
- Menor aderência ao modelo Java EE corporativo

---

## Consequences

### Positivas

- Deploy único simplificado
- Integração automática entre módulos
- Alta consistência transacional
- Compatibilidade total com WildFly
- Modelo alinhado com ambientes enterprise legados

---

### Negativas

- Atualização exige redeploy completo
- Menor flexibilidade de versionamento por módulo
- Escalabilidade não granular (tudo ou nada)

---

## Evolution Strategy

Embora o EAR seja a unidade inicial de deploy, a arquitetura permite evolução futura.

### Possíveis evoluções:

- Separação do WAR em frontend desacoplado (SPA)
- Extração de EJBs para serviços independentes
- Migração gradual para arquitetura distribuída

### Estratégia de evolução:

- Aplicação do Strangler Pattern
- Extração progressiva de módulos críticos
- Introdução de APIs REST entre camadas
- Eventual migração para microsserviços

---

## Scope

Esta decisão se aplica a:

- Modelo de build Maven
- Estratégia de deploy no :contentReference[oaicite:1]{index=1}
- Organização dos módulos do sistema HSG HIS
- Ciclo de release do MVP

---

## References

- Jakarta EE Specification
- Enterprise Application Architecture Patterns
- WildFly Deployment Guide
- Java EE EAR Packaging Model
- Strangler Pattern (Martin Fowler)