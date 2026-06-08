# HSG HIS — Hospital Information System

Sistema de Informação Hospitalar do **Hospital São Gabriel (HSG)** — plataforma server-side Java EE que cobre o ciclo completo de atendimento ambulatorial: agendamento, atendimento clínico, prontuário longitudinal, receituário, anexos clínicos, notificações e identidade unificada.

Desenvolvido como simulação de ambiente corporativo realista, sobre stack **Java EE 8 + WildFly + JSF/PrimeFaces + PostgreSQL + Keycloak + S3-compatible (MinIO/S3)**, com evolução incremental por MVPs.

> ⚠ Repositório com **artefatos DEV-only** (seed SQL e realm Keycloak contêm secrets em texto plano). Leia [`docs/operacao/seguranca-dev-vs-prd.md`](docs/operacao/seguranca-dev-vs-prd.md) antes de promover para HML/PRD.

---

## 1. Objetivo do sistema

Atender o fluxo clínico-administrativo de uma clínica/hospital de médio porte:

- Paciente agenda, confirma, cancela e remarca consultas pelo portal
- Recepção (enfermeiro/admin) faz check-in, registra faltas, gerencia agenda do dia
- Médico atende, registra observação clínica, anota durante e após a consulta, anexa documentos, emite receituário e visualiza o prontuário longitudinal do paciente
- Admin gerencia médicos, grades de agenda, convênios e planos
- Identidade unificada via Keycloak (4 perfis: paciente, médico, enfermeiro, admin)
- Notificações in-app + e-mail automáticas em cada evento relevante

---

## 2. O que ESTE projeto é

✅ **HIS funcional para uso clínico-administrativo cotidiano** com:

| Módulo | Estado |
|--------|--------|
| Agendamento (paciente/médico/admin, slots, grades, exceções, busca) | **Entregue** |
| Convênio (planos, regras de cobertura, copagamento, carteirinha) | **Entregue** |
| Atendimento clínico (check-in, realizada, falta, cancelamento) | **Entregue** |
| Histórico de status auditado (cada transição registrada) | **Entregue** |
| Anotações livres por consulta (N por consulta, multi-perfil) | **Entregue** |
| Anexos / Exames clínicos (storage S3-compatible, PDF/JPG/PNG/WebP) | **Entregue** |
| Receituário médico simples (PDF on-demand com logo da clínica) | **Entregue** |
| Prontuário longitudinal do paciente (visão cross-consulta + PDF) | **Entregue** |
| Notificações in-app + e-mail | **Entregue** |
| Auto-falta (job agendado diário) | **Entregue** |
| Identidade unificada (Keycloak OIDC) | **Entregue** |
| Suite de testes (564 unitários, 0 falhas) | **Entregue** |

---

## 3. O que ESTE projeto NÃO é (escopo fora)

❌ **Não é um HIS hospitalar de larga escala** com:

- Internação / leitos / centro cirúrgico / UTI
- Receitas controladas (notificação Anvisa C1/B1 com retenção legal) — apenas receitas simples
- Atestados médicos eletrônicos com ICP-Brasil — atestados são entregues fisicamente pelo médico
- Telemedicina / vídeo consulta (WebRTC)
- Cobrança / boleto / PIX / faturamento financeiro
- Integração com PACS / DICOM real
- Antecedentes médicos manuais (cirurgias prévias, doenças crônicas) — futuro
- Sinais vitais com gráfico de evolução — futuro
- Dashboard executivo com KPIs / BI
- Integração com e-SUS / cartão SUS
- Mobile app nativo

❌ **Não é produto pronto para produção** sem:

- Substituir 100% dos secrets em `init.sql`, `hsg-realm.json` e `.env`
- Trocar chaves de criptografia (`CPF_ENCRYPTION_KEY`, `CARTEIRINHA_ENCRYPTION_KEY`)
- Habilitar TLS/HTTPS, SSE-S3 nos buckets, versioning + IAM real
- Procedimentos completos em [`docs/operacao/seguranca-dev-vs-prd.md`](docs/operacao/seguranca-dev-vs-prd.md)

---

## 4. Stack técnica

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 8 |
| Server | WildFly 26 (Jakarta EE 8 / Java EE 7) |
| Frontend | JSF 2.3 + PrimeFaces 6.2 (server-side rendering) |
| Negócio | EJB 3.2 (`@Stateless`, `@Singleton`, `@Schedule`) |
| Persistência | JPA 2.2 / Hibernate 5.3 |
| Banco | PostgreSQL 15 |
| Migrations | Flyway 9 (V1–V33) |
| Auth | Keycloak 26 (OIDC + realm import) |
| E-mail (DEV) | MailHog |
| Storage | MinIO (DEV) / AWS S3 ou DigitalOcean Spaces (PRD) via AWS SDK v2 |
| PDF | OpenPDF 1.3.30 (receituário, prontuário) |
| Build | Maven multi-módulo, package EAR |
| Container | Docker / Docker Compose v2 |
| Testes | JUnit 4 + Mockito |

---

## 5. Arquitetura

Monolito modular em camadas (decisão registrada em [ADR004](docs/adrs/ADR004-padrao-de-camadas-domain-service-web.md)):

```
┌───────────────────────────────────────────────────────────┐
│  Browser (Paciente / Médico / Enfermeiro / Admin)         │
└──────────────────────────┬────────────────────────────────┘
                           │ HTTPS (OIDC via Keycloak)
                           ▼
┌───────────────────────────────────────────────────────────┐
│  hsg-his-web (WAR — JSF/PrimeFaces + Servlets dedicados) │
│  ↕                                                        │
│  hsg-his-service (EJB — Facades + Impls + Schedulers)    │
│  ↕                                                        │
│  hsg-his-domain (Entities + VOs + Enums)                 │
└───────┬────────────┬────────────┬──────────────────┬──────┘
        ▼            ▼            ▼                  ▼
    PostgreSQL    MinIO/S3     Keycloak           MailHog/SMTP
    (schema       (4 buckets)  (realm OIDC)       (notificações)
     hsg)
```

| Módulo Maven | Pacote | Empacotamento |
|--------------|--------|---------------|
| `hsg-his-domain`  | Entidades JPA, enums, VOs, factories | JAR |
| `hsg-his-service` | EJBs (regras de negócio), DAOs, DTOs, PDF builders | EJB JAR |
| `hsg-his-web`     | Beans JSF, XHTML, servlets (download/PDF) | WAR |
| `hsg-his-ear`     | Empacotamento de deploy | EAR |

Decisões em [`docs/adrs/`](docs/adrs/). ADRs cobrem: stack legado, arquitetura EAR, multi-módulo, camadas, Keycloak, JPA, WildFly, storage buckets separados, path lógico vendor-agnostic.

---

## 6. Estrutura do repositório

```
hsg-his-web-app/
├── hsg-his-domain/             # entidades + enums + VOs
├── hsg-his-service/            # EJBs + DAOs + DTOs + PDF builders
├── hsg-his-web/                # JSF + PrimeFaces + Servlets
├── hsg-his-ear/                # empacotamento EAR
├── infra/
│   ├── app/docker/             # Dockerfile WildFly + app
│   ├── keycloak/               # imagem KC + realm DEV
│   ├── wildfly/                # config WildFly (datasources, modules)
│   └── db/
│       ├── init/init-dbs.sql   # cria bancos (hsg + keycloak)
│       ├── migrations/V1..V33  # Flyway
│       └── seed/init.sql       # massa DEV (APP_ENV=DEV)
├── docs/                       # módulos, ADRs, regras, operação, branding
├── .env.example                # template (commitado)
├── .env                        # real (gitignored)
├── docker-compose.yml          # stack completa parametrizada
└── pom.xml                     # POM pai (Maven multi-módulo)
```

---

## 7. Pré-requisitos

| Item | Versão mínima | Comentário |
|------|---------------|------------|
| **Docker Engine** | 24+ | obrigatório (sobe stack completa) |
| **Docker Compose** | v2.20+ | embutido no Docker Desktop ≥ 4.x |
| **Recursos do host** | 4 vCPU, 8 GB RAM, 10 GB disco livre | WildFly + Keycloak + Postgres + MinIO juntos consomem ~5 GB RAM |
| **Portas livres** | 8180, 8080, 9000, 9001, 8025, 1025, 5432 | conflitos? ajustar no `.env` |
| **Java 8 / Maven** | (opcional) | só pra build sem container (`./mvnw clean install`) |
| **`curl`** | (opcional) | usado em healthchecks/smoke tests |

Validar tudo OK:

```bash
docker --version       # ≥ 24
docker compose version # ≥ v2.20
docker info            # confirma daemon rodando
```

---

## 8. Como executar

### 8.1 Setup inicial (uma vez)

```bash
git clone <repo>
cd hsg-his-web-app
cp .env.example .env          # bootstrap do .env (já no .gitignore)
# (opcional) edite .env pra trocar portas/senhas
```

### 8.2 Subir stack completa

```bash
docker compose up -d --build
```

Ordem automática: `postgres` → `flyway` (migrate) → `seed` (carrega massa DEV) → `keycloak` + `mailhog` + `minio` + `minio-init` → `app`.

Boot completo leva ~2–3 minutos no primeiro `up` (build da imagem app + import do realm Keycloak).

### 8.3 Endpoints e portas

| Serviço | URL | Credencial demo |
|---------|-----|-----------------|
| **Aplicação** | http://localhost:8180/hsg-his | (via Keycloak) |
| Keycloak admin | http://localhost:8080 | `admin` / `Admin@HSG2026` |
| MailHog (inbox) | http://localhost:8025 | — |
| MinIO console | http://localhost:9001 | `minioadmin` / `minioadmin` |
| MinIO API S3 | http://localhost:9000 | mesmas credenciais |
| PostgreSQL | localhost:5432 | `postgres` / `postgres` (DB `hsg`) |

### 8.4 Credenciais demo (seed DEV)

| Perfil | Login | Senha |
|--------|-------|-------|
| Paciente | `claudio.filho` | `Cliente@2026` |
| Paciente | `mariana.santos` | `Cliente@2026` |
| Médico | `dr.joao` (Clínica Médica) | `Medico@2026` |
| Médico | `dr.roberto` (Clínica Médica) | `Medico@2026` |
| Médico | `dra.fernanda` (Neurologia) | `Medico@2026` |
| Enfermeiro | `enf.maria` | `Enfermeiro@2026` |
| Admin | `admin.hsg` | `Admin@2026` |

Lista completa em [`infra/keycloak/realm/hsg-realm.json`](infra/keycloak/realm/hsg-realm.json).

### 8.5 Logs e troubleshooting

```bash
docker compose logs -f app           # app
docker compose logs -f keycloak      # auth
docker compose logs -f postgres      # db
docker compose ps                    # status
docker compose down                  # parar tudo (preserva volumes)
docker compose down -v               # reset total (apaga banco/MinIO)
```

### 8.6 Build local sem container (opcional)

```bash
./mvnw clean install                 # build + 564 testes
./mvnw -pl hsg-his-ear -am package -DskipTests   # só package
```

EAR gerado em `hsg-his-ear/target/hsg-his-ear-1.0-SNAPSHOT.ear`.

---

## 9. Documentação

Tudo organizado em [`docs/`](docs/). Pontos de entrada:

| Tópico | Local |
|--------|-------|
| **Índice geral** | [`docs/README.md`](docs/README.md) |
| Visão de negócio | [`docs/visao-geral.md`](docs/visao-geral.md) |
| **Módulos por feature** | [`docs/modulos/`](docs/modulos/) — agendamento, atendimento clínico, status, histórico, anotações, notificações, storage, receituário, prontuário |
| **Modelo de dados** | [`docs/dominio/`](docs/dominio/) |
| **Regras de negócio** | [`docs/regras/regras-negocio-agendamento.md`](docs/regras/regras-negocio-agendamento.md) — IDs AG-/CA-/AC-/AF-/AN-/AX-/RC-/PR- |
| **ADRs** (decisões) | [`docs/adrs/`](docs/adrs/) — 9 documentos |
| **Operação** | [`docs/operacao/`](docs/operacao/) — storage-config, antivirus (stub), **segurança DEV vs PRD** |
| **Brand assets** | [`docs/branding/brand-assets.md`](docs/branding/brand-assets.md) |
| **UI / Design system** | [`docs/ui/`](docs/ui/) |

---

## 10. Configuração via `.env`

Toda parametrização do compose vai via `.env` (template em [`.env.example`](.env.example)). Categorias:

- **Postgres** — usuário, senha, nome do banco
- **App** — porta, env (`APP_ENV=DEV` controla seed), URLs base, chaves de criptografia
- **Keycloak** — admin, realm, client, URLs, porta
- **Mail** — SMTP (MailHog em DEV)
- **MinIO / Storage** — credenciais, endpoint, region, path-style, buckets por domínio

Em DEV os defaults do compose já funcionam sem `.env`. Em HML/PRD: gerar `.env` real com senhas reais, secrets em cofre, endpoints S3 produtivos.

---

## 11. Roadmap (próximas evoluções possíveis)

| Bloco | Prioridade |
|-------|-----------|
| Antecedentes médicos manuais (cirurgias, doenças crônicas) | Alta |
| Sinais vitais com gráfico de evolução | Alta |
| Dashboard executivo (KPIs admin + agenda do dia médico) | Alta |
| Cobrança PIX / boleto (encerra loop financeiro) | Média |
| CI/CD GitHub Actions (badge no README) | Média |
| Lista de espera por slot lotado | Média |
| Lembrete de confirmação 24h antes | Média |
| Telemedicina (WebRTC) | Baixa |
| App mobile | Baixa |

Mais detalhes na seção "Recomendações de evolução" das discussões internas.

---

## 12. Licença e uso

Projeto desenvolvido como simulação de ambiente corporativo. Massa de seed contém dados fictícios. Antes de usar em qualquer cenário real, ver checklist completo em [`docs/operacao/seguranca-dev-vs-prd.md`](docs/operacao/seguranca-dev-vs-prd.md).
