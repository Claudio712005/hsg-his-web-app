# Configuração de Storage — DEV vs PRD

Guia operacional do módulo storage de arquivos. Cobre setup local (MinIO), promoção para PRD AWS S3, e troca futura para outras clouds (DigitalOcean Spaces, Azure Blob).

---

## 1. Variáveis de ambiente

| Variável | DEV (MinIO) | PRD AWS S3 | PRD Azure Blob (futuro) | Descrição |
|---|---|---|---|---|
| `APP_STORAGE_ENDPOINT`               | `http://minio:9000` | *(unset)* | *(endpoint Azure)* | Sobrescreve endpoint. Vazio = SDK usa default AWS. |
| `APP_STORAGE_REGION`                 | `us-east-1`         | `sa-east-1`              | n/a | Region do storage |
| `APP_STORAGE_ACCESS_KEY`             | `minioadmin`        | (IAM user ou role)       | (account key)       | Credencial |
| `APP_STORAGE_SECRET_KEY`             | `minioadmin`        | (secret IAM)             | (account key)       | Credencial |
| `APP_STORAGE_PATH_STYLE`             | `true`              | `false`                  | n/a                 | MinIO exige path-style. S3 prefere virtual-host. |
| `APP_STORAGE_PRESIGN_TTL_MIN`        | `15`                | `15`                     | `15`                | TTL de URL pré-assinada |
| `APP_STORAGE_MAX_BYTES`              | `20971520`          | `52428800`               | `52428800`          | Tamanho máximo por arquivo (default 20 MB DEV, 50 MB PRD) |
| `APP_STORAGE_BUCKET_ANEXO_CLIENTE`   | `hsg-anexos-cliente`  | `hsg-prod-anexos-cliente`  | `anexos-cliente`  | Bucket por domínio |
| `APP_STORAGE_BUCKET_ANEXO_CONSULTA`  | `hsg-anexos-consulta` | `hsg-prod-anexos-consulta` | `anexos-consulta` | Bucket por domínio |
| `APP_STORAGE_BUCKET_ANEXO_ANOTACAO`  | `hsg-anexos-anotacao` | `hsg-prod-anexos-anotacao` | `anexos-anotacao` | Bucket por domínio |
| `APP_STORAGE_BUCKET_EXAME_CONSULTA`  | `hsg-exames-consulta` | `hsg-prod-exames-consulta` | `exames-consulta` | Bucket por domínio |

Em DEV os valores default vêm do [`docker-compose.yml`](../../docker-compose.yml). Em PRD vêm de Secrets/ConfigMap do OpenShift.

---

## 2. Setup local (MinIO)

`docker compose up -d minio minio-init` sobe o MinIO e cria os 4 buckets com versioning habilitado.

- API S3: `http://localhost:9000`
- Console web: `http://localhost:9001` (login `minioadmin/minioadmin`)
- Volume persistente: `hsg_minio_data`

Para validar manualmente:

```bash
docker run --rm --network hsg-network minio/mc:latest \
  sh -c 'mc alias set local http://minio:9000 minioadmin minioadmin && mc ls local'
```

Saída esperada: 4 buckets `hsg-anexos-{cliente,consulta,anotacao}` + `hsg-exames-consulta`.

Para inspecionar versioning:

```bash
mc version info local/hsg-anexos-consulta
# → local/hsg-anexos-consulta versioning is enabled
```

---

## 3. Setup PRD AWS S3

### 3.1 Buckets

Criar 4 buckets manualmente (CloudFormation/Terraform recomendado em produção real):

```
hsg-prod-anexos-cliente
hsg-prod-anexos-consulta
hsg-prod-anexos-anotacao
hsg-prod-exames-consulta
```

Configurações obrigatórias por bucket:
- **Versioning**: Enabled (recuperação de delete acidental)
- **Encryption at rest**: SSE-S3 (gerenciada AWS) — default automático no S3 desde 2023
- **Block all public access**: ON
- **Lifecycle policy** (opcional):
  - `hsg-prod-exames-consulta`: transição para Glacier após 365 dias (compliance: reter 5 anos)
  - `hsg-prod-anexos-anotacao`: retenção indefinida (auditoria prontuário)

### 3.2 IAM policy mínima

Usuário IAM da aplicação precisa de:

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": [
      "s3:PutObject",
      "s3:GetObject",
      "s3:DeleteObject",
      "s3:HeadObject"
    ],
    "Resource": [
      "arn:aws:s3:::hsg-prod-anexos-cliente/*",
      "arn:aws:s3:::hsg-prod-anexos-consulta/*",
      "arn:aws:s3:::hsg-prod-anexos-anotacao/*",
      "arn:aws:s3:::hsg-prod-exames-consulta/*"
    ]
  }]
}
```

Em ambiente OpenShift/AWS preferir **IAM Role for Service Account (IRSA)** em vez de access keys hardcoded.

### 3.3 Env vars no OpenShift

`oc set env deployment/hsg-app APP_STORAGE_ENDPOINT-` (remove para usar default), depois `oc set env deployment/hsg-app APP_STORAGE_PATH_STYLE=false APP_STORAGE_REGION=sa-east-1 APP_STORAGE_BUCKET_*=...` etc.

Credenciais em `Secret` separado, montadas como env vars.

---

## 4. Setup PRD DigitalOcean Spaces (alternativa)

Spaces é S3-compatível. Configuração:
- `APP_STORAGE_ENDPOINT=https://nyc3.digitaloceanspaces.com`
- `APP_STORAGE_REGION=nyc3`
- `APP_STORAGE_PATH_STYLE=false`
- Buckets: `hsg-prod-anexos-cliente`, etc.

Lifecycle policy via console DO (não suporta API completa AWS).

---

## 5. Troubleshooting

| Sintoma | Causa provável | Solução |
|---|---|---|
| `403 SignatureDoesNotMatch` no DEV | `APP_STORAGE_PATH_STYLE=false` contra MinIO | Confirmar `true` em DEV |
| `404 NoSuchBucket` | Bucket não criado ou nome divergente da env var | `mc ls local` (DEV) ou `aws s3 ls` (PRD) |
| Download retorna HTML do MinIO ao invés do arquivo | `APP_STORAGE_ENDPOINT` aponta para hostname não resolvível pelo browser quando usando presigned URL | Usar proxy via app (atual default) ou expor MinIO em hostname público |
| `RegionMissingException` | `APP_STORAGE_REGION` vazia | Setar pelo menos `us-east-1` mesmo em MinIO |
| `ConnectionRefused` em PRD | IAM sem permissão ou endpoint errado | Validar role/policy AWS |
| Upload trava em 100% sem confirmação | `multipart-config` no `web.xml` com limite menor que `APP_STORAGE_MAX_BYTES` | Ajustar `<max-file-size>` |

---

## 6. Encryption at rest

| Ambiente | Estratégia | Estado |
|---|---|---|
| DEV (MinIO) | SSE-S3 exige KES/KMS configurado — overkill local. Não habilitado. | OFF |
| PRD (AWS S3) | SSE-S3 (gerenciada AWS) — default automático em buckets novos. | ON |
| PRD (DO Spaces) | SSE-S3 (gerenciada DO) | ON |
| PRD (Azure Blob, futuro) | Server-side encryption padrão | ON |

Encryption in transit: TLS sempre. MinIO em DEV roda em HTTP sem TLS (rede docker interna). PRD HTTPS obrigatório.

---

## 7. Versioning

Habilitado em todos buckets, DEV e PRD. Permite recuperar objeto deletado por:

```bash
# DEV
mc cp --version <versionId> local/hsg-anexos-consulta/<key> ./recovered.pdf

# PRD AWS
aws s3api list-object-versions --bucket hsg-prod-anexos-consulta --prefix <key>
aws s3api get-object --bucket hsg-prod-anexos-consulta --key <key> --version-id <versionId> ./recovered.pdf
```

---

## 8. GC de arquivos órfãos (a implementar)

Job futuro (`StorageGcScheduler` em `service/impl/scheduler/`):

1. `ArquivoDAO.listarInativosParaGc(limite)` retorna linhas `st_arquivo='I'` mais antigas
2. Para cada: `storage.delete(pathLogico)` + remover linha DB
3. Agendar diário às 02:00 via `@Schedule(hour="2", minute="0")`

Stub documentado em `storage-antivirus.md` (compartilha pipeline de scan).
