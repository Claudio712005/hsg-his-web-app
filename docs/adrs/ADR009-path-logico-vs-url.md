# ADR-009 — Path lógico no DB (vs URL completa)

**Status**: Aceito
**Data**: 2026-06-06
**Contexto**: Implementação do módulo de storage de arquivos.

## Decisão

O DB grava apenas o **path lógico** do arquivo (ex.: `/anexos/consulta/45/2026/06/<uuid>.pdf`). O bucket físico e a URL final são resolvidos em runtime pelo `StoragePathResolver` + `ObjectStorageService`.

## Drivers

1. **Vendor-agnostic** — mesmo path serve para AWS S3, MinIO, DigitalOcean Spaces ou Azure Blob. Troca de cloud não exige migração de DB.
2. **Pré-assinatura sob demanda** — URL com signature expira em ~15 min. Gravar URL no DB seria garbage instantâneo. Pré-assinar a cada download mantém a URL fresca.
3. **Hostname pode mudar** — se MinIO migrar de `minio:9000` para `storage.internal:9000`, ou S3 mudar de region, o DB segue íntegro.
4. **Disaster recovery facilitado** — backup do DB + buckets idempotentes em qualquer cloud permite reconstruir todo o sistema. Não há tracking de URLs históricas que precisem ser reescritas.
5. **Separação de responsabilidades** — DB conhece a estrutura de domínio (quem é dono do arquivo). Storage conhece a localização física. Camadas trocáveis.

## Alternativas consideradas

| Alternativa | Por que recusada |
|---|---|
| Gravar URL completa do S3 | Acopla DB ao provedor; URLs pré-assinadas expiram; trocar cloud reescreve todo histórico. |
| Gravar `bucket` + `key` separados | Acopla DB ao conceito de bucket (que pode não existir em outras clouds — Azure Blob usa "container"); resolver pelo prefixo é igualmente eficiente. |
| Gravar apenas UUID + buscar via convenção | Perde rastreabilidade do domínio sem fazer query extra; quebra paginação por domínio. |

## Consequências

**Positivas**:
- Migração entre clouds = trocar `ObjectStorageService` impl + reuploadar buckets. DB intacto.
- URLs pré-assinadas geradas com TTL ajustável por env var
- Domínio embutido no path facilita observabilidade (grep no DB já mostra distribuição)

**Negativas**:
- Cada operação de download exige lookup `path → bucket` via resolver (custo desprezível, é EnumMap em memória)
- Não funciona com storage que não seja S3-compatível sem nova implementação de `ObjectStorageService` (mas isso é positivo do ponto de vista de design)

## Implementação

- Path lógico construído por `StoragePathResolver.buildLogicalPath(dominio, ownerId, filename)`
- Formato: `<prefixo>/<ownerId>/yyyy/MM/<uuid>.<ext>`
- Reverso: `StoragePathResolver.resolve(pathLogico) → BucketBinding(bucket, objectKey)` via `StorageDomain.pelaPrefixoDoPathLogico`
- Path gravado na coluna `ds_path_logico` (UNIQUE) em `tb_arquivo`

## Referência

Ver `docs/modulos/storage-arquivos.md` e `docs/dominio/modelo-arquivo.md`.
