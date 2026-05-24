# Documentação — HSG HIS

Este diretório concentra toda a documentação técnica, funcional e arquitetural do sistema **HSG HIS (Hospital São Gabriel Saúde)**.

O objetivo é manter o conhecimento do sistema organizado, rastreável e alinhado com as decisões arquiteturais ao longo da evolução do projeto.

---

## 1. Objetivo da Documentação

A documentação existe para:

- Centralizar decisões técnicas e de negócio
- Evitar perda de conhecimento arquitetural
- Padronizar evolução do sistema
- Facilitar onboarding de novos desenvolvedores
- Garantir rastreabilidade de decisões (ADRs)

---

## 2. Estrutura da Documentação

### 📁 Requisitos

Contém o escopo funcional do sistema organizado por MVPs.

- MVP-1: fluxo básico hospitalar (paciente, consulta, atendimento)
- ...

---

### 📁 ADRs (Architecture Decision Records)

Registra decisões arquiteturais importantes do sistema.

Exemplos:

- Escolha de stack Java EE
- Uso de EAR como unidade de deploy
- Estratégia de persistência
- Integração com Keycloak
- Deploy em OpenShift

---

### 📁 Arquitetura

Contém visão técnica do sistema:

- Arquitetura em camadas
- Diagramas
- Visão de módulos Maven
- Integração entre componentes

---

### 📁 Domínio

Contém documentação do modelo de negócio:

- Entidades principais do sistema
- Regras de negócio
- Glossário hospitalar (abreviações e termos internos)

---

### 📁 UI (Interface)

Define padrões visuais do sistema:

- Paleta de cores
- Layout padrão
- Componentes reutilizáveis
- Diretrizes de UX para sistema hospitalar

---

## 3. Padrão de Documentação

Toda documentação segue os seguintes princípios:

- Escrita em Markdown
- Versionamento junto ao código
- Evolução incremental (não reescrita total)
- Decisões técnicas registradas via ADR
- Separação clara entre negócio e arquitetura

---

## 4. Relação com o Sistema

A documentação acompanha diretamente a arquitetura do sistema:

- Backend Java EE (JSF + EJB + JPA)
- Deploy em :contentReference[oaicite:0]{index=0}
- Orquestração via :contentReference[oaicite:1]{index=1}
- Autenticação via :contentReference[oaicite:2]{index=2}

---

## 5. Evolução da Documentação

A documentação será evolutiva e acompanhará:

- Crescimento dos MVPs
- Novas decisões arquiteturais (ADRs)
- Mudanças no domínio hospitalar
- Evolução da infraestrutura

---

## 6. Regras Importantes

- Nenhuma decisão arquitetural deve existir apenas em código
- Todo MVP deve ter rastreabilidade em `/requisitos`
- Toda decisão técnica relevante deve gerar um ADR
- Mudanças arquiteturais devem ser versionadas

---

## 7. Convenção de Uso

- Requisitos → escopo do sistema
- ADRs → decisões técnicas
- Arquitetura → visão estrutural
- Domínio → regras de negócio
- UI → padrão visual

---

## 8. Visão Geral

Este diretório funciona como a **fonte única de verdade arquitetural e funcional do sistema HSG HIS**, garantindo clareza e consistência ao longo de toda a evolução do projeto.