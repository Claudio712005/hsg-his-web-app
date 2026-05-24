# ADR-005: Autenticação e Autorização com Keycloak

## Status

Aprovada

---

## Context

O sistema HSG HIS possui múltiplos perfis de usuários e fluxos críticos de acesso, incluindo:

- Recepção hospitalar
- Médicos
- Administração
- Futuras áreas como faturamento e auditoria

O controle de acesso precisa atender requisitos como:

- Autenticação centralizada
- Controle de permissões por perfil (RBAC)
- Integração com aplicação Java EE
- Suporte a ambiente corporativo (SSO futuro)
- Segurança consistente entre módulos

O sistema será executado em :contentReference[oaicite:0]{index=0}, utilizando JSF como camada de apresentação.

Foram consideradas as seguintes abordagens:

- Autenticação local via JAAS/Filters
- Implementação customizada de login no JSF
- Uso de Identity Provider externo (Keycloak)

---

## Decision

Será adotado o uso do :contentReference[oaicite:1]{index=1} como provedor central de autenticação e autorização do sistema.

A integração será realizada com o sistema Java EE utilizando mecanismos compatíveis com WildFly e camada JSF.

---

## Justificativa

### 1. Centralização de autenticação

O Keycloak permite centralizar:

- Login de usuários
- Gestão de sessões
- Políticas de segurança

**Impacto:**

- Eliminação de autenticação duplicada
- Padronização do controle de acesso

---

### 2. Controle de acesso baseado em papéis (RBAC)

O sistema hospitalar possui perfis bem definidos:

- ROLE_RECEPCAO
- ROLE_MEDICO
- ROLE_ADMIN

O Keycloak suporta:

- Roles globais e por cliente
- Grupos de usuários
- Mapeamento de permissões

**Impacto:**

- Segurança mais consistente
- Redução de lógica de segurança na aplicação

---

### 3. Integração com Java EE

O Keycloak possui suporte a integração com:

- Aplicações JSF
- Servidores como WildFly
- Protocolos padrão como OAuth2 e OpenID Connect

**Impacto:**

- Integração segura e padronizada
- Redução de código customizado de autenticação

---

### 4. Preparação para SSO e expansão futura

O uso de Keycloak permite:

- Single Sign-On (SSO)
- Integração com sistemas externos
- Federação de identidade (LDAP/AD)

**Impacto:**

- Escalabilidade organizacional
- Integração com ecossistemas hospitalares maiores

---

### 5. Redução de complexidade na aplicação

Sem Keycloak, a aplicação teria que gerenciar:

- Sessões
- Senhas
- Criptografia
- Controle de acesso

**Impacto:**

- Redução de responsabilidade da aplicação
- Melhor separação de concerns

---

## Alternatives Considered

### Autenticação local (JSF + Session)

**Prós:**

- Simplicidade inicial
- Menor dependência externa

**Contras:**

- Baixa escalabilidade
- Segurança limitada
- Difícil manutenção de permissões

---

### JAAS nativo do Java EE

**Prós:**

- Integração com servidor de aplicação
- Sem dependência externa

**Contras:**

- Baixa flexibilidade
- Complexidade de configuração
- Tecnologia considerada legada

---

## Consequences

### Positivas

- Centralização da segurança
- Controle robusto de acesso
- Suporte a SSO futuro
- Padronização enterprise
- Redução de código de autenticação na aplicação

---

### Negativas

- Dependência de serviço externo
- Necessidade de infraestrutura adicional
- Curva de aprendizado inicial
- Complexidade de integração no início do projeto

---

## Evolution Strategy

O modelo de segurança pode evoluir para:

### Possíveis extensões:

- Integração com LDAP hospitalar
- Autenticação federada entre sistemas
- MFA (Multi-Factor Authentication)
- Integração com provedores externos

### Estratégia:

- Manter Keycloak como fonte única de identidade
- Mapear roles do Keycloak para permissões internas
- Evitar lógica de autorização espalhada no código

---

## Scope

Esta decisão se aplica a:

- Autenticação do sistema HSG HIS
- Controle de acesso no frontend JSF
- Autorização em serviços EJB
- Integração com :contentReference[oaicite:2]{index=2} e :contentReference[oaicite:3]{index=3}

---

## References

- Keycloak Documentation
- OAuth2 / OpenID Connect Specification
- Java EE Security Architecture
- Role-Based Access Control (RBAC) Model