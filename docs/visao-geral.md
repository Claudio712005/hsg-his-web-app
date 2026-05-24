# Visão Geral — HSG HIS (Hospital São Gabriel Saúde)

## 1. Contexto do Cliente

O **Hospital São Gabriel Saúde (HSG)** é uma instituição hospitalar de médio porte localizada em uma capital brasileira, com atuação em:

- Atendimento de urgência e emergência
- Internação clínica e cirúrgica
- Consultas ambulatoriais
- Exames laboratoriais e de imagem

A instituição atende convênios e pacientes particulares, com alta demanda diária em pronto atendimento.

---

## 2. Situação Atual (Cenário Legado)

O hospital opera atualmente com um conjunto de sistemas desconectados entre si, adquiridos ao longo dos anos:

- Sistema de agendamento médico (desktop legado)
- Sistema de prontuário eletrônico parcialmente funcional
- Planilhas paralelas para controle de leitos
- Sistema de faturamento independente
- Controle manual em setores críticos (recepção e internação)

Não existe integração entre os sistemas, o que obriga duplicação de dados e processos manuais.

---

## 3. Principais Problemas Identificados

### 3.1 Fragmentação de dados
- Paciente cadastrado em múltiplos sistemas
- Inconsistência de informações clínicas e administrativas

### 3.2 Baixa rastreabilidade clínica
- Histórico do paciente incompleto
- Dificuldade para auditoria médica e hospitalar

### 3.3 Processos manuais
- Controle de leitos via planilhas
- Comunicação entre setores por telefone e papel

### 3.4 Alto tempo de atendimento
- Retrabalho na recepção
- Falta de centralização do fluxo de consulta

### 3.5 Falta de controle de segurança
- Acessos sem padronização de perfis
- Ausência de controle centralizado de autenticação

---

## 4. Problema Central

O Hospital São Gabriel Saúde não possui um sistema integrado de gestão hospitalar, resultando em:

- Baixa eficiência operacional
- Risco clínico elevado
- Dificuldade de expansão e auditoria
- Dependência de processos manuais

---

## 5. Objetivo do Sistema

Desenvolver o **HSG HIS (Hospital Information System)** com o objetivo de:

- Centralizar o cadastro de pacientes
- Integrar agendamento, consulta e atendimento
- Controlar fluxo hospitalar (recepção → consulta → atendimento)
- Garantir rastreabilidade clínica
- Padronizar acesso e segurança

---

## 6. Escopo Inicial (MVP-1)

O primeiro MVP terá foco no fluxo básico hospitalar:

### 6.1 Cadastro de Pacientes
- Inclusão, edição e consulta de pacientes

### 6.2 Agendamento de Consultas
- Marcação de consultas por médico e especialidade
- Controle de horários

### 6.3 Atendimento Médico
- Registro de atendimento
- Observações clínicas básicas
- Encerramento de atendimento

### 6.4 Perfis de Usuário
- Recepção
- Médico
- Administração

---

## 7. Visão de Evolução do Sistema

Após o MVP inicial, o sistema evoluirá para:

- Gestão de internação e leitos
- Faturamento hospitalar
- Integração com laboratórios
- Dashboard gerencial
- Relatórios clínicos e administrativos

---

## 8. Arquitetura Prevista

O sistema será baseado em arquitetura Java EE tradicional, utilizando:

- JSF para camada de apresentação
- EJB para regras de negócio
- JPA para persistência
- WildFly como servidor de aplicação
- Keycloak para autenticação e autorização
- Deploy via Kubernetes/OpenShift

---

## 9. Premissa do Projeto

O sistema é propositalmente baseado em tecnologias corporativas e legadas, com o objetivo de simular ambientes reais de grandes instituições hospitalares que ainda operam com Java EE tradicional.

---

## 10. Resultado Esperado

Ao final do MVP-1, espera-se:

- Fluxo hospitalar funcional ponta a ponta
- Base sólida para evolução modular
- Padronização mínima de arquitetura e UI
- Ambiente pronto para escalabilidade e integração futura