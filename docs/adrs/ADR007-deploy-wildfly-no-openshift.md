# ADR-007: Estratégia de Deploy no OpenShift com WildFly

## Status

Aprovada

---

## Context

O sistema HSG HIS será executado em ambiente corporativo com necessidade de padronização de infraestrutura, escalabilidade e automação de deploy.

A aplicação será empacotada como EAR e executada em :contentReference[oaicite:0]{index=0}, utilizando tecnologias Java EE legadas (JSF, EJB e JPA).

O ambiente alvo será um cluster baseado em Kubernetes, utilizando :contentReference[oaicite:1]{index=1} como plataforma de orquestração.

O desafio arquitetural consiste em definir:

- Forma de deploy da aplicação
- Estratégia de containerização
- Modelo de escalabilidade
- Ciclo de release em ambiente containerizado

Foram consideradas as seguintes abordagens:

- Deploy manual em servidor físico/VM
- Deploy automatizado via CI/CD em containers
- Migração para arquitetura cloud-native (Spring Boot / microsserviços)

---

## Decision

Será adotada uma estratégia de deploy baseada em **containerização do WildFly com aplicação EAR, orquestrada via OpenShift**.

O sistema será distribuído como:

- Imagem Docker contendo WildFly configurado
- Artefato EAR implantado no servidor de aplicação
- Deploy gerenciado por OpenShift (pods e services)

---

## Justificativa

### 1. Compatibilidade com arquitetura legada

O uso de WildFly com EAR mantém compatibilidade com:

- Java EE tradicional
- JSF + EJB
- Modelo monolítico corporativo

**Impacto:**

- Permite simulação de ambientes hospitalares reais
- Evita necessidade de refatoração arquitetural

---

### 2. Padronização de infraestrutura com containers

Com OpenShift:

- Aplicação é executada em containers padronizados
- Ambiente é reproduzível em qualquer cluster
- Elimina dependência de servidores físicos

**Impacto:**

- Maior consistência entre ambientes (dev, stage, prod)
- Redução de erros de configuração

---

### 3. Escalabilidade horizontal controlada

Mesmo sendo monólito:

- É possível escalar instâncias do WildFly horizontalmente
- Load balancing gerenciado pelo OpenShift

**Impacto:**

- Suporte a aumento de carga sem alteração arquitetural
- Escalabilidade por réplica de aplicação

---

### 4. Automação de deploy (CI/CD)

OpenShift permite integração com pipelines:

- Build automatizado de imagem Docker
- Deploy automático do EAR
- Versionamento de releases

**Impacto:**

- Redução de deploy manual
- Maior confiabilidade em releases

---

### 5. Separação entre aplicação e infraestrutura

A aplicação não depende do servidor físico:

- WildFly encapsulado em container
- Configuração gerenciada por ambiente
- Deploy desacoplado de infraestrutura

**Impacto:**

- Portabilidade entre ambientes
- Facilidade de migração futura

---

## Alternatives Considered

### Deploy em VM tradicional

**Prós:**

- Simplicidade inicial
- Menor curva de aprendizado

**Contras:**

- Baixa escalabilidade
- Configuração manual
- Difícil automação

---

### Microsserviços em Kubernetes

**Prós:**

- Escalabilidade independente
- Deploy desacoplado por serviço

**Contras:**

- Alta complexidade arquitetural
- Problemas de consistência distribuída
- Não alinhado ao objetivo de simulação de legado

---

### Spring Boot cloud-native

**Prós:**

- Moderno
- Alta produtividade
- Forte ecossistema cloud

**Contras:**

- Foge do objetivo de simulação Java EE legado
- Requer reestruturação completa da arquitetura

---

## Consequences

### Positivas

- Compatibilidade com arquitetura Java EE
- Uso eficiente de containers
- Escalabilidade horizontal via OpenShift
- Automação de deploy com CI/CD
- Ambiente replicável e padronizado

---

### Negativas

- Dependência de infraestrutura OpenShift
- Complexidade inicial de configuração de containers
- Overhead do WildFly dentro de container
- Escalabilidade limitada ao nível do monólito

---

## Evolution Strategy

A estratégia de deploy pode evoluir conforme o crescimento do sistema:

### Possíveis evoluções:

- Separação de módulos em serviços independentes (microsserviços)
- Substituição parcial do WildFly por runtimes mais leves
- Introdução de arquitetura híbrida (monólito + serviços auxiliares)
- Migração para comunicação baseada em eventos

### Estratégia:

- Manter o EAR como unidade inicial de deploy
- Utilizar OpenShift como base de escalabilidade
- Aplicar Strangler Pattern para evolução gradual
- Monitorar gargalos por módulo antes de qualquer refatoração

---

## Scope

Esta decisão se aplica a:

- Estratégia de deploy do HSG HIS
- Infraestrutura de execução em OpenShift
- Containerização da aplicação Java EE
- Execução do :contentReference[oaicite:2]{index=2}

---

## References

- OpenShift Documentation
- Kubernetes Architecture Principles
- WildFly Deployment Guide
- Java EE Packaging Standards
- Cloud Native Deployment Patterns