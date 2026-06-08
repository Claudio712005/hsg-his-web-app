# ADR-008 — Buckets separados por domínio (vs prefixos em bucket único)

**Status**: Aceito
**Data**: 2026-06-06
**Contexto**: Implementação do módulo de storage de arquivos (anexos, exames).

## Decisão

Usar **buckets separados** por `StorageDomain` (`hsg-anexos-cliente`, `hsg-anexos-consulta`, `hsg-anexos-anotacao`, `hsg-exames-consulta`) ao invés de um único bucket com prefixos lógicos (`hsg-arquivos/anexos/cliente/...`, `hsg-arquivos/exames/...`).

## Drivers

1. **IAM policy independente por domínio** — paciente pode receber permissão de leitura em `exames-consulta` sem expor `anexos-anotacao`. Com bucket único, permissões por prefixo são possíveis mas verbosas e propensas a erro.
2. **Lifecycle e retenção diferentes** — exames têm requisito legal de 5 anos; anotações clínicas, 20+ anos por compliance. Lifecycle policy é por bucket no AWS S3.
3. **Billing e métricas por domínio** — CloudWatch e cost allocation tags rendem visibilidade direta por bucket.
4. **Migração entre clouds por bucket** — futura migração de "exames" para um storage especializado (PACS médico) não obriga mover todos os anexos.
5. **Versioning e encryption configuráveis por bucket** — permite tratar buckets de paciente diferente dos buckets clínicos.

## Alternativas consideradas

| Alternativa | Por que recusada |
|---|---|
| Bucket único `hsg-arquivos` com prefixos | IAM por prefixo é mais complexo de auditar; lifecycle por prefixo é AWS-only e limitado; perde granularidade futura. |
| Bucket por paciente | Inviável (mil+ pacientes = mil+ buckets, limite AWS de 100 buckets por conta sem soft-limit increase). |
| Bucket por consulta | Mesmo problema, escala explosiva. |

## Consequências

**Positivas**:
- IAM granular sem regex de path
- Lifecycle/retenção/versioning configurados por domínio
- Migração entre clouds isolada por bucket

**Negativas**:
- Mais buckets para gerenciar (4 hoje, escala linear se novos domínios)
- Boot-time do MinIO em DEV precisa criar 4 buckets (`minio-init` script)
- Cada novo `StorageDomain` exige nova env var + bucket provisionado em PRD

## Implementação

- `StorageDomain` enum em [`hsg-his-domain`](../../hsg-his-domain/src/main/java/br/com/hsg/domain/enums/StorageDomain.java) — fonte da verdade dos domínios
- `StoragePathResolverImpl` lê env vars `APP_STORAGE_BUCKET_<DOMINIO>` no startup e mapeia em `EnumMap<StorageDomain, String>`
- `S3ObjectStorageServiceImpl` reutiliza um único `S3Client` para todos buckets (mesma região + credenciais)
- `minio-init` no [`docker-compose.yml`](../../docker-compose.yml) cria 4 buckets em loop, idempotente

## Referência

Ver `docs/modulos/storage-arquivos.md` para overview do módulo.
