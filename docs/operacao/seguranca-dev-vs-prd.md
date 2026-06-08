# Segurança — Artefatos DEV-only

⚠ **LEIA ANTES DE PROMOVER PARA HML/PRD.**

Vários arquivos no repositório foram desenhados **exclusivamente** para acelerar onboarding e testes locais. Contêm **secrets em texto claro** e **NÃO devem ser usados em ambientes produtivos** sem substituição completa.

---

## 1. Artefatos DEV-only neste repositório

| Caminho | Conteúdo sensível | Por que existe |
|---------|-------------------|----------------|
| [`infra/db/seed/init.sql`](../../infra/db/seed/init.sql) | Usuários demo (paciente, médico, enfermeiro, admin), CPFs/RGs placeholder, IDs Keycloak fixos | Carregar massa de teste consistente em DEV (consulta + anotação + anexo + receita + prontuário). Roda automático quando `APP_ENV=DEV`. |
| [`infra/keycloak/realm/hsg-realm.json`](../../infra/keycloak/realm/hsg-realm.json) | Realm completo com **senhas em texto plano**, client secret, IDs estáveis | Importação automática do realm no boot do Keycloak (`start-dev --import-realm`). Sem ele o developer teria que criar tudo manualmente. |
| [`.env.example`](../../.env.example) | Defaults de senha (`Admin@HSG2026`, `minioadmin`, `postgres`), chaves de criptografia placeholder | Servir de template do `.env` real |
| Defaults dentro do [`docker-compose.yml`](../../docker-compose.yml) | Mesmas senhas placeholder via `${VAR:-default}` | Garantir que `docker compose up` funciona sem `.env` configurado |

---

## 2. Por que esses arquivos **NÃO** vão pra PRD

- **Senhas em texto plano** — `init.sql` e `hsg-realm.json` contêm `password: senha123`-equivalente. Quem clona o repo já tem todas as credenciais
- **IDs do Keycloak fixos** (`md000000-0000-0000-0000-000000000001` etc) — qualquer atacante que conheça o repo público sabe os IDs internos de cada usuário demo
- **Hashes determinísticos** de CPF/RG (`dev_placeholder_cpf_hash_*`) — não passam por uma validação real e expõem o esquema de hashing
- **Chave de criptografia `HSG_DEV_ONLY_PLACEHOLDER_32CHAR_`** — qualquer cópia desencripta dados produtivos se for usada lá
- **Bucket policies** abertos pra teste, sem versioning/encryption rigorosos
- **`APP_ENV=DEV`** dispara o seed automaticamente — em PRD esse env vira `PRD` para o script abortar

---

## 3. Promoção pra HML/PRD — checklist obrigatório

| # | Ação | Onde |
|---|------|------|
| 1 | **Não use `init.sql`**. Em HML/PRD os pacientes/usuários nascem dos fluxos da aplicação (auto-cadastro, convite Keycloak, importação controlada). Marque `APP_ENV=HML` ou `APP_ENV=PRD` para o serviço `seed` no compose pular. | `.env` produção |
| 2 | **Substitua `hsg-realm.json`** por importação manual via console do Keycloak, OU mantenha um realm.json **sem senhas e sem client secret** (placeholder `<<TROCAR>>`) e configure tudo pós-import via API admin. | `infra/keycloak/realm/hsg-realm-prd.json` (cria) |
| 3 | **Troque todas as senhas** no `.env` real: `POSTGRES_PASSWORD`, `KEYCLOAK_ADMIN_PASSWORD`, `MINIO_ROOT_PASSWORD`, credenciais S3 reais (IAM IRSA recomendado) | `.env` produção |
| 4 | **Gere chaves de criptografia novas** (32 chars aleatórios) para `CPF_ENCRYPTION_KEY` e `CARTEIRINHA_ENCRYPTION_KEY`. Guarde em cofre (Vault/Secrets Manager). | Secret manager |
| 5 | **Habilite SSE-S3 + versionamento** nos buckets reais (já documentado em [`storage-config.md`](storage-config.md)). | Bucket lifecycle |
| 6 | **Force HTTPS no Keycloak** — `KC_HOSTNAME_STRICT_HTTPS=true`, certificado válido, atrás de proxy/Ingress. | Deploy manifest |
| 7 | **Remova MailHog do compose** e configure SMTP corporativo via env (SES/SendGrid/Postmark). | Deploy manifest |
| 8 | **Desative `start-dev`** do Keycloak em PRD — usar `start` com `--optimized`, `KC_LOG_LEVEL=WARN`. | `keycloak.command` |
| 9 | **Audite o repo** com `git log --all -p -- infra/db/seed infra/keycloak/realm` antes de tornar público pra confirmar que histórico não vaza credenciais reais usadas em algum momento. | Pré-publicação |
| 10 | **Rotação periódica** de credenciais Postgres/MinIO/Keycloak admin no ambiente real. | Calendário ops |

---

## 4. Onde estão referenciados

Para qualquer mudança no esquema de "DEV-only", atualizar também:

- [`docker-compose.yml`](../../docker-compose.yml) — verifica `APP_ENV` no serviço `seed`
- [`.env.example`](../../.env.example) — comentário "PRD: troque tudo que tem PLACEHOLDER"
- [`infra/keycloak/README.md`](../../infra/keycloak/README.md) — quando promover, indicar realm de HML/PRD
- [`docs/operacao/storage-config.md`](storage-config.md) — credenciais MinIO vs S3 IAM real
- Este documento (`seguranca-dev-vs-prd.md`)

---

## 5. Como rodar **sem** o seed em DEV (smoke test de "como seria em PRD")

```bash
APP_ENV=STAGE docker compose up -d
```

Faz o serviço `seed` reconhecer que não é DEV e abortar — o banco fica apenas com o schema das migrations Flyway, sem usuários demo. Útil para validar fluxos de cadastro real.

---

## 6. Resumo em 1 linha

`init.sql` e `hsg-realm.json` são **fixtures de desenvolvimento**, não configuração de produção. Tratá-los como tal evita vazamento massivo de credenciais quando o repo é tornado público ou colaborativo.
