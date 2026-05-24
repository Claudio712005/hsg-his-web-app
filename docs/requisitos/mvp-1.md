# MVP-1 — Guia Completo de Entrega (HSG HIS)

## 1. Objetivo do MVP-1

O MVP-1 do sistema HSG HIS tem como objetivo validar o fluxo básico hospitalar dentro de um ambiente Java EE legado, incluindo:

- Cadastro de pacientes
- Agendamento de consultas
- Registro de atendimento médico
- Controle básico de usuários por perfil

A entrega deve simular um ambiente corporativo real, utilizando arquitetura baseada em :contentReference[oaicite:0]{index=0} com JSF, EJB e JPA.

---

## 2. Escopo Funcional (MVP-1)

### 2.1 Pacientes

- Criar paciente
- Editar paciente
- Consultar paciente
- Listar pacientes

Campos básicos:
- Nome
- CPF
- Data de nascimento
- Telefone
- Status (ativo/inativo)

---

### 2.2 Agendamento de Consultas

- Criar agendamento
- Listar agendamentos por médico
- Cancelar agendamento

Regras:
- Não permitir conflito de horário
- Consulta vinculada a paciente e médico

---

### 2.3 Atendimento Médico

- Abrir atendimento
- Registrar observações clínicas
- Finalizar atendimento

Regras:
- Atendimento só pode ser iniciado a partir de uma consulta agendada

---

### 2.4 Usuários e Perfis

Perfis iniciais:

- RECEPCAO
- MEDICO
- ADMIN

Regras:
- Acesso baseado em roles vindas do :contentReference[oaicite:1]{index=1}

---

## 3. Escopo Não Funcional

### 3.1 Arquitetura

- Arquitetura monólito modular
- Java EE tradicional
- JSF (UI)
- EJB (serviços)
- JPA (persistência)
- Empacotamento EAR

---

### 3.2 Infraestrutura

Ambiente baseado em containers:

- Runtime: :contentReference[oaicite:2]{index=2}
- Orquestração: :contentReference[oaicite:3]{index=3}
- Banco de dados: PostgreSQL (containerizado)
- Identity Provider: :contentReference[oaicite:4]{index=4}

---

### 3.3 Requisitos de infraestrutura

- Aplicação deve rodar em container Docker
- Deploy via OpenShift
- Banco de dados externo ao container da aplicação
- Configuração via variáveis de ambiente
- Suporte a múltiplos ambientes:
    - dev
    - homolog
    - produção

---

## 4. Arquitetura de Deploy

### 4.1 Estrutura de artefato

- EAR como unidade principal de deploy
- Contém:
    - WAR (JSF)
    - EJB JAR (services)
    - Domain JAR (entities)

---

### 4.2 Estratégia de deploy

- Build gera artefato EAR
- Pipeline constrói imagem Docker com WildFly
- OpenShift realiza rollout automático

---

## 5. Pipeline CI/CD

### 5.1 Etapas do pipeline

1. Checkout do código
2. Build Maven (`mvn clean install`)
3. Execução de testes unitários
4. Build do EAR
5. Build da imagem Docker
6. Push para registry
7. Deploy no OpenShift

---

### 5.2 Branch strategy (GitFlow simplificado)

- `main` → produção
- `develop` → integração
- `feature/*` → desenvolvimento de funcionalidades
- `release/*` → preparação de release
- `hotfix/*` → correções críticas

---

## 6. Requisitos de Qualidade

### 6.1 Testes

- Testes unitários (JUnit)
- Testes de serviço (EJB layer)
- Testes de integração (mínimo no MVP)

---

### 6.2 Cobertura esperada

- Regras de negócio críticas: obrigatório teste
- Persistência: testes de integração básicos
- UI: validação manual inicial

---

## 7. Requisitos de Segurança

- Autenticação via :contentReference[oaicite:5]{index=5}
- Autorização baseada em roles
- Sessão gerenciada pelo container
- Proteção de endpoints JSF por perfil

---

## 8. Requisitos de Performance

- Resposta de telas principais < 2s
- Consultas paginadas obrigatórias
- Lazy loading em tabelas JSF
- Evitar carregamento massivo de entidades JPA

---

## 9. Requisitos de Observabilidade

- Logs centralizados (console/container)
- Logs por nível:
    - INFO (fluxos)
    - WARN (inconsistências)
    - ERROR (falhas)
- Preparação para integração futura com:
    - Prometheus
    - Grafana

---

## 10. Entregáveis do MVP-1

- Sistema funcional de cadastro de pacientes
- Fluxo de agendamento completo
- Atendimento médico básico
- Autenticação via Keycloak
- Deploy em OpenShift
- Pipeline CI/CD funcional
- EAR gerado e deployável

---

## 11. Critérios de Aceite

O MVP-1 será considerado concluído quando:

- Todos os fluxos principais estiverem funcionais
- Sistema estiver rodando em ambiente containerizado
- Autenticação e roles estiverem integradas
- Deploy automatizado estiver operacional
- Nenhuma funcionalidade crítica depender de execução manual

---

## 12. Evolução pós-MVP

Após o MVP-1, o sistema evoluirá para:

- Gestão de leitos
- Faturamento hospitalar
- Integração com laboratórios
- Dashboards gerenciais
- Relatórios clínicos avançados