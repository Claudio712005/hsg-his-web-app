# ADR-003: Estrutura Multi-Módulo Maven para Organização do Sistema

## Status

Aprovada

---

## Context

O sistema HSG HIS será desenvolvido com base em uma arquitetura Java EE modular, composta por diferentes responsabilidades técnicas:

- Camada de apresentação (JSF + PrimeFaces)
- Camada de negócio (EJB)
- Camada de domínio (entidades JPA)
- Empacotamento final (EAR)

Sem uma estrutura modular adequada, o projeto tende a evoluir para:

- Alto acoplamento entre camadas
- Dificuldade de manutenção
- Baixa reutilização de código
- Crescimento desordenado do código-fonte

Além disso, o ambiente de execução será baseado em :contentReference[oaicite:0]{index=0}, o que favorece organização modular com deploy unificado.

Foram consideradas as seguintes abordagens:

- Projeto Maven único (monólito sem módulos)
- Multi-módulo Maven
- Múltiplos projetos independentes

---

## Decision

Será adotada uma arquitetura **multi-módulo Maven (Maven Multi-Module Project)**, centralizada em um único projeto pai.

A estrutura será composta por:

- `hsg-his` (POM raiz / agregador)
- `hsg-his-domain` (JAR)
- `hsg-his-service` (EJB)
- `hsg-his-web` (WAR)
- `hsg-his-ear` (EAR)

O projeto raiz atuará como agregador e gerenciador de build.

---

## Justificativa

### 1. Separação clara de responsabilidades

Cada módulo terá uma responsabilidade bem definida:

- Domain → modelo de dados e entidades
- Service → regras de negócio
- Web → interface do usuário
- EAR → empacotamento

**Impacto:**

- Melhor organização do código
- Redução de acoplamento lógico
- Maior legibilidade arquitetural

---

### 2. Reutilização e isolamento de código

A separação em módulos permite:

- Reutilização do domínio por diferentes camadas
- Isolamento de dependências
- Compilação independente por módulo

**Impacto:**

- Builds mais eficientes
- Menor impacto de mudanças locais
- Melhor controle de dependências

---

### 3. Alinhamento com arquitetura Java EE

O modelo multi-módulo se alinha diretamente com:

- JSF (camada web)
- EJB (serviços)
- JPA (domínio)
- EAR (empacotamento)

**Impacto:**

- Compatibilidade com padrões corporativos
- Facilidade de deploy no WildFly

---

### 4. Escalabilidade organizacional do código

Mesmo em um monólito modular, a separação permite:

- Crescimento independente de cada camada
- Possível extração futura de módulos
- Organização alinhada a bounded contexts

---

### 5. Padronização de build e CI/CD

Com Maven multi-module:

- Um único comando de build (`mvn clean install`)
- Build ordenado por dependência
- Integração facilitada com pipelines de CI/CD

---

## Alternatives Considered

### Projeto Maven único (sem módulos)

**Prós:**

- Simplicidade inicial
- Menos configuração

**Contras:**

- Alto acoplamento
- Dificuldade de manutenção
- Crescimento desorganizado do código

---

### Múltiplos projetos independentes

**Prós:**

- Total independência entre serviços
- Deploy separado

**Contras:**

- Complexidade de integração
- Gestão de dependências manual
- Não adequado ao modelo Java EE monolítico com EAR

---

## Consequences

### Positivas

- Organização clara do código
- Baixo acoplamento entre camadas
- Build estruturado e previsível
- Compatibilidade com EAR e WildFly
- Facilita evolução arquitetural futura

---

### Negativas

- Curva inicial de configuração maior
- Necessidade de disciplina arquitetural
- Dependência de build centralizado

---

## Evolution Strategy

A estrutura multi-módulo foi desenhada para permitir evolução controlada.

### Possíveis evoluções:

- Extração de módulos em serviços independentes
- Separação do frontend em aplicação desacoplada
- Criação de bounded contexts mais rígidos

### Estratégia:

- Manter domínio como núcleo central
- Evoluir módulos de service para APIs externas
- Aplicar Strangler Pattern quando necessário

---

## Scope

Esta decisão se aplica a:

- Estrutura de projeto Maven
- Organização do código-fonte
- Estratégia de build e CI/CD
- Integração com :contentReference[oaicite:1]{index=1}

---

## References

- Maven Multi-Module Project Documentation
- Clean Architecture (Robert C. Martin)
- Domain-Driven Design (Eric Evans)
- Java EE / Jakarta EE Architecture Patterns