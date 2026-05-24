# HSG HIS — Keycloak

Infraestrutura de Identity & Access Management para o HSG HIS usando **Keycloak 26**.

## Pré-requisitos

- Docker Engine 24+
- Docker Compose v2.20+
- `curl` (para healthcheck)

---

## Iniciar (stack completa — recomendado)

```bash
# Na raiz do projeto
cp .env.example .env        # edite as senhas
docker compose up -d        # sobe PostgreSQL + Keycloak
docker compose logs -f keycloak   # acompanha os logs
```

Aguarde a mensagem **Keycloak 26.x.x on JVM** nos logs.

Acesse: <http://localhost:8080>

---

## Iniciar somente o Keycloak (standalone)

```bash
docker compose -f infra/keycloak/docker/docker-compose.yml up -d
```

---

## Primeiro build (após alterar o tema)

```bash
docker compose build keycloak
docker compose up -d keycloak
```

---

## Consoles

| Endereço | Descrição |
|---|---|
| <http://localhost:8080/admin> | Console de administração |
| <http://localhost:8080/realms/hsg-his/account> | Portal do usuário |
| <http://localhost:8080/realms/hsg-his/.well-known/openid-configuration> | OIDC Discovery |

Credenciais de admin (dev): `admin / Admin@HSG2026`

---

## Realm `hsg-his`

Importado automaticamente na primeira inicialização via `--import-realm`.

| Configuração | Valor |
|---|---|
| Login Theme | `hsg-theme` |
| Registro | desativado |
| Remember Me | ativado |
| Reset de senha | ativado |
| Login com e-mail | ativado |
| Proteção brute-force | ativado (5 tentativas) |

### Roles

| Role | Descrição |
|---|---|
| `hsg-admin` | Administrador do sistema |
| `hsg-medico` | Médico |
| `hsg-enfermeiro` | Enfermeiro |
| `hsg-atendente` | Atendente |
| `hsg-farmaceutico` | Farmacêutico |
| `hsg-paciente` | Paciente (portal) |

### Usuários de desenvolvimento

| Usuário | Senha (temporária) | Role |
|---|---|---|
| `admin.hsg` | `Admin@HSG2026` | hsg-admin |
| `dr.joao` | `Medico@HSG2026` | hsg-medico |
| `enf.maria` | `Enf@HSG2026` | hsg-enfermeiro |

> As senhas são **temporárias** — Keycloak pedirá troca no primeiro login.

---

## Estrutura de arquivos

```
infra/keycloak/
├── docker/
│   ├── Dockerfile            # Imagem customizada (tema built-in)
│   └── docker-compose.yml    # Compose standalone (Keycloak-only)
├── realm/
│   └── hsg-realm.json        # Realm completo (roles, clients, users)
├── themes/
│   └── hsg-theme/
│       └── login/
│           ├── login.ftl
│           ├── theme.properties
│           ├── messages/
│           └── resources/ (css, js)
├── scripts/
│   ├── import-realm.sh       # Importação manual via kcadm
│   └── create-client.sh      # Recriação do client via kcadm
└── README.md
```

---

## Reimportar o realm (se necessário)

```bash
chmod +x infra/keycloak/scripts/import-realm.sh
./infra/keycloak/scripts/import-realm.sh
```

## Recriar o client da aplicação

```bash
chmod +x infra/keycloak/scripts/create-client.sh
APP_BASE_URL=http://localhost:8180 ./infra/keycloak/scripts/create-client.sh
```

---

## Parar e limpar

```bash
docker compose down          # para os containers (preserva volumes)
docker compose down -v       # para E remove os volumes (dados perdidos)
docker rmi hsg-keycloak:local  # remove a imagem customizada
```
