# HSG HIS — Hospital Information System

Sistema de Gestão Hospitalar do **Hospital São Gabriel Saúde (HSG)**, desenvolvido como simulação de ambiente corporativo baseado em arquitetura Java EE legada, com foco em domínio hospitalar realista e evolução incremental via MVPs.

---

## 1. Objetivo do Sistema

O HSG HIS tem como objetivo simular um sistema hospitalar real, cobrindo:

- Gestão de pacientes (cadastro, endereço, alergias, tipo sanguíneo)
- Pré-cadastro de profissionais (médicos e enfermeiros) via convite por e-mail
- Gestão de convênios, planos e regras de cobertura
- Vínculo paciente–convênio e solicitações de convênio
- Especialidades médicas
- Controle de acesso por perfis (médico, enfermeiro, admin)

O sistema reflete cenários corporativos encontrados em instituições de saúde de médio porte. Contexto detalhado em [docs/visao-geral.md](docs/visao-geral.md).

---

## 2. Stack Tecnológica

- **Java EE 7** (Jakarta EE legado), compilado em Java 8
- **JSF 2.3 + PrimeFaces 6.2** — frontend server-side
- **EJB 3.2** — regras de negócio
- **JPA 2.2** — persistência
- **Maven multi-módulo** — build, empacotamento **EAR**
- **WildFly** — servidor de aplicação
- **PostgreSQL 15** — banco de dados
- **Flyway** — versionamento de schema (migrations)
- **Keycloak** — autenticação e autorização (OIDC)
- **MailHog** — captura de e-mails em ambiente de desenvolvimento
- **Docker / Docker Compose** — containerização e orquestração local

---

## 3. Arquitetura

Arquitetura monolítica modular (padrão de camadas — ver [ADR004](docs/adrs/ADR004-padrao-de-camadas-domain-service-web.md)):

| Módulo | Responsabilidade |
|--------|------------------|
| `hsg-his-domain`  | Entidades e modelo de domínio (JPA) |
| `hsg-his-service` | Regras de negócio (EJB) |
| `hsg-his-web`     | Camada de apresentação (JSF + PrimeFaces) |
| `hsg-his-ear`     | Empacotamento e deploy (EAR) |

Decisões de arquitetura documentadas em [docs/adrs/](docs/adrs/).

---

## 4. Estrutura do Repositório

```
hsg-his/
├── hsg-his-domain/      # entidades de domínio
├── hsg-his-service/     # EJBs / regras de negócio
├── hsg-his-web/         # JSF / PrimeFaces
├── hsg-his-ear/         # empacotamento EAR
├── infra/
│   ├── app/docker/      # Dockerfile do WildFly + app
│   ├── keycloak/        # imagem e realm do Keycloak
│   ├── wildfly/         # configuração do WildFly
│   └── db/
│       ├── migrations/  # migrations Flyway (V1..V20)
│       └── seed/        # dados de desenvolvimento (APP_ENV=DEV)
├── docs/                # visão, ADRs, requisitos, UI
├── docker-compose.yml
└── pom.xml              # POM pai
```

---

## 5. Execução do Projeto

### 5.1 Subir todo o ambiente (recomendado)

`docker-compose` sobe banco, migrations, seed, Keycloak, MailHog e a aplicação:

```bash
docker compose up --build
```

Ordem de inicialização: `postgres` → `flyway` (migrate) → `seed` → `keycloak` + `mailhog` → `app`.

### 5.2 Serviços e portas

| Serviço | URL / Porta | Observação |
|---------|-------------|------------|
| Aplicação (WildFly) | http://localhost:8180/hsg-his | context root `hsg-his` |
| Keycloak | http://localhost:8080 | admin / `Admin@HSG2026` |
| MailHog (UI) | http://localhost:8025 | inbox de e-mails (dev) |
| PostgreSQL | localhost:5432 | `postgres` / `postgres` |

### 5.3 Build local (Maven)

```bash
./mvnw clean install
```

O artefato final é o EAR gerado em `hsg-his-ear/target/`.

---

## 6. Banco de Dados

- Schema versionado via **Flyway** em [infra/db/migrations/](infra/db/migrations/) (`V1` … `V20`).
- O serviço `seed` carrega dados de desenvolvimento somente quando `APP_ENV=DEV`.
- Banco padrão: `hsg` (schema `hsg`). Keycloak usa banco próprio `keycloak`.

---

## 7. Documentação

- [Visão Geral](docs/visao-geral.md) — contexto do cliente e problema
- [Requisitos — MVP 1](docs/requisitos/mvp-1.md)
- [ADRs](docs/adrs/) — decisões de arquitetura
- [UI](docs/ui/) — design system, paleta, tipografia, layout
