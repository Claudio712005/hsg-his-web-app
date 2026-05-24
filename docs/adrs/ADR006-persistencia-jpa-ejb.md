# ADR-006: Estratégia de Persistência com JPA e EJB

## Status

Aprovada

---

## Context

O sistema HSG HIS possui um domínio hospitalar fortemente orientado a dados, envolvendo entidades como:

- Pacientes
- Médicos
- Consultas
- Atendimentos
- Usuários e perfis

Esses dados exigem:

- Consistência transacional
- Relacionamentos complexos entre entidades
- Persistência confiável em banco relacional
- Integração com servidor de aplicação corporativo

O sistema será executado em :contentReference[oaicite:0]{index=0}, utilizando Java EE tradicional com EJB e JSF.

Foram consideradas as seguintes abordagens:

- JDBC puro
- Spring Data JPA
- JPA com EJB (container-managed persistence)

---

## Decision

Será adotada a estratégia de persistência utilizando:

- :contentReference[oaicite:1]{index=1} como padrão de mapeamento ORM
- :contentReference[oaicite:2]{index=2} como camada de serviços de acesso a dados
- EntityManager gerenciado pelo container (Container-Managed EntityManager)

A persistência será centralizada no módulo:

- `hsg-his-domain` → entidades JPA
- `hsg-his-service` → EJBs responsáveis por operações de persistência

---

## Justificativa

### 1. Integração nativa com Java EE

A combinação JPA + EJB é padrão no ecossistema Java EE:

- JPA gerencia o mapeamento objeto-relacional
- EJB gerencia transações e ciclo de vida
- WildFly fornece suporte nativo a ambos

**Impacto:**

- Redução de configuração manual
- Integração transparente entre camadas

---

### 2. Consistência transacional (ACID)

O domínio hospitalar exige forte consistência:

- Atualização de status de atendimento
- Registro de consultas
- Persistência de prontuários

Com EJB + JPA:

- Transações gerenciadas pelo container (JTA)
- Commit/Rollback automático
- Garantia de atomicidade

---

### 3. Redução de boilerplate

Comparado a JDBC puro:

- Elimina SQL manual em grande parte dos casos
- Reduz código repetitivo de conexão e mapeamento
- Facilita manutenção

---

### 4. Modelagem orientada ao domínio

JPA permite modelagem próxima ao domínio:

- Entidades representam o negócio
- Relacionamentos entre entidades são declarativos
- Facilita leitura e evolução do modelo

---

### 5. Alinhamento com arquitetura legado corporativo

Sistemas Java EE tradicionais utilizam:

- JPA como padrão de persistência
- EJB como camada de negócio
- Banco relacional como fonte principal

**Impacto:**

- Simulação realista de ambiente hospitalar corporativo
- Compatibilidade com infraestruturas legadas

---

## Alternatives Considered

### JDBC puro

**Prós:**

- Controle total sobre SQL
- Performance otimizada em casos específicos

**Contras:**

- Alto custo de manutenção
- Muito boilerplate
- Baixa produtividade

---

### Spring Data JPA

**Prós:**

- Produtividade alta
- Repositórios prontos
- Ecossistema moderno

**Contras:**

- Fora do padrão Java EE legado
- Adiciona camada externa desnecessária ao objetivo do projeto

---

## Consequences

### Positivas

- Integração nativa com :contentReference[oaicite:3]{index=3}
- Consistência transacional forte
- Modelagem orientada ao domínio
- Redução de código repetitivo
- Compatibilidade com arquitetura legada

---

### Negativas

- Menor flexibilidade comparado a soluções modernas
- Dependência do container de aplicação
- Debug de queries pode ser menos direto
- Curva de aprendizado para otimizações JPA

---

## Evolution Strategy

A estratégia de persistência pode evoluir futuramente para:

### Possíveis melhorias:

- Introdução de queries otimizadas (JPQL/Native SQL)
- Cache de segundo nível (Hibernate L2 cache)
- Separação de read/write models (CQRS parcial)
- Extração de serviços com bancos independentes (em evolução futura para microsserviços)

### Estratégia:

- Manter JPA como padrão central
- Isolar lógica de persistência dentro do módulo de serviço
- Evitar vazamento de SQL para camadas superiores

---

## Scope

Esta decisão se aplica a:

- Camada de persistência do HSG HIS
- Módulo `hsg-his-domain` (entidades)
- Módulo `hsg-his-service` (EJBs)
- Integração com :contentReference[oaicite:4]{index=4}

---

## References

- Jakarta Persistence API (JPA) Specification
- EJB Specification
- Java EE Architecture Patterns
- Domain-Driven Design (DDD)
- Enterprise Application Architecture (Martin Fowler)