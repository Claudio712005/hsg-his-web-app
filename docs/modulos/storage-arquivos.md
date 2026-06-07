# Storage de Arquivos (Anexos e Exames)

Módulo responsável por upload, download e gestão de metadata de arquivos vinculados a consultas, anotações ou diretamente ao paciente. Funciona contra **MinIO local** em DEV e contra **AWS S3** (ou qualquer storage S3-compatível) em PRD, sem alterações no código de aplicação — só nas variáveis de ambiente.

---

## 1. Princípio

O banco grava apenas o **path lógico** do arquivo (ex.: `/anexos/consulta/45/2026/06/<uuid>.pdf`). O bucket físico é resolvido em tempo de execução pelo [`StoragePathResolver`](../../hsg-his-service/src/main/java/br/com/hsg/service/facade/storage/StoragePathResolver.java) com base no domínio embutido no prefixo do path. Trocar de S3 para Azure Blob (ou outra cloud) muda só a implementação de [`ObjectStorageService`](../../hsg-his-service/src/main/java/br/com/hsg/service/facade/storage/ObjectStorageService.java) — o DB nunca sabe quem hospeda os bytes.

ADRs relacionados:
- [ADR-001 storage buckets separados](../adrs/ADR008-storage-buckets-separados.md)
- [ADR-002 path lógico vs URL](../adrs/ADR009-path-logico-vs-url.md)

---

## 2. Domínios de armazenamento (`StorageDomain`)

| Domínio | Prefixo lógico | Bucket DEV (MinIO) | Bucket PRD (S3) | Caso de uso |
|---|---|---|---|---|
| `ANEXO_CLIENTE`  | `/anexos/cliente`  | `hsg-anexos-cliente`  | `hsg-prod-anexos-cliente`  | Documentos do paciente fora de consulta (RG, comprovantes) |
| `ANEXO_CONSULTA` | `/anexos/consulta` | `hsg-anexos-consulta` | `hsg-prod-anexos-consulta` | Anexos médicos durante atendimento (receitas, atestados) |
| `ANEXO_ANOTACAO` | `/anexos/anotacao` | `hsg-anexos-anotacao` | `hsg-prod-anexos-anotacao` | Imagens/PDFs vinculados a uma anotação clínica específica |
| `EXAME_CONSULTA` | `/exames/consulta` | `hsg-exames-consulta` | `hsg-prod-exames-consulta` | Exames trazidos pelo paciente ou solicitados |

Buckets separados (vs prefixos em bucket único) escolhidos por:
- IAM policy independente por domínio (paciente vs clínica)
- Lifecycle/retenção pode diferir (exames > 5 anos, anexos clínicos > 20 anos por compliance)
- Billing e métricas por domínio
- Migração entre clouds por bucket sem afetar os demais

---

## 3. Pipeline de upload

1. Bean recebe `FileUploadEvent` da UI (`p:fileUpload`)
2. Bean chama `ArquivoServiceFacade.anexarEm{Consulta|Exame|Anotacao}(...)`
3. Service:
   - Valida autorização (regras AX-03, AX-04, AX-05)
   - Chama [`StorageGuard.validar`](../../hsg-his-service/src/main/java/br/com/hsg/service/impl/storage/StorageGuard.java): content-type whitelist (PDF/JPEG/PNG/WebP), tamanho ≤ `APP_STORAGE_MAX_BYTES`, sanitiza filename, valida magic bytes vs Content-Type
   - Chama `StoragePathResolver.buildLogicalPath(dominio, ownerId, filename)` → gera `/<prefixo>/<ownerId>/yyyy/MM/<uuid>.<ext>`
   - Chama `ObjectStorageService.put(path, stream, size, ct)` → upload pro bucket resolvido
   - Calcula SHA-256 do payload
   - Persiste `Arquivo` via `ArquivoDAO.salvar` com metadata + path lógico
4. UI recarrega lista de anexos

---

## 4. Pipeline de download

Implementação atual: **proxy via app**. Bean expõe `StreamedContent` via `<p:fileDownload>`. App busca metadata, valida autorização, chama `storage.get(path)`, devolve bytes ao cliente.

Vantagens: funciona idêntico DEV/PRD, autorização centralizada, sem expor URL pré-assinada do MinIO (que tem hostname interno `minio:9000` não resolvido pelo browser).

Trade-off: cada download passa pelo WildFly. Para PRD em escala alta, ativar `urlDownload` que devolve URL pré-assinada do S3 (direto cliente → S3) — método já existe em `ArquivoServiceFacade.urlDownload`, basta trocar o XHTML para usar URL.

---

## 5. Regras de autorização (AX-*)

| ID | Regra | Onde |
|----|-------|------|
| AX-01 | Content-Type na whitelist (`application/pdf`, `image/jpeg`, `image/png`, `image/webp`) + magic bytes batem | [`StorageGuard.validarContentType` / `validarMagicBytes`](../../hsg-his-service/src/main/java/br/com/hsg/service/impl/storage/StorageGuard.java) |
| AX-02 | Tamanho ≤ `APP_STORAGE_MAX_BYTES` (default 20 MB) | `StorageGuard.validarTamanho` |
| AX-03 | Médico só anexa nas próprias consultas/anotações | `ArquivoServiceImpl.anexarEm*` |
| AX-04 | Paciente anexa exame só em consulta própria em `AGENDADA`/`CONFIRMADA` | `ArquivoServiceImpl.anexarExameEmConsulta` |
| AX-05 | Consulta `CANCELADA` não aceita anexos | `ArquivoServiceImpl.bloquearSeCancelada` |
| AX-06 | Filename sanitizado (path traversal, controle, acentos) | `StorageGuard.sanitizeFilename` |

Autorização de **leitura**:
- ADMIN / ENFERMEIRO: qualquer arquivo
- MEDICO: só arquivos de consultas próprias (verificação por `id_consulta` → `medico.id`)
- PACIENTE: só arquivos do próprio paciente (verificação por `id_paciente` ou `id_consulta.paciente.id`)

Autorização de **remoção**:
- Autor original (mesmo `id_responsavel` + `tp_responsavel`) **ou** ADMIN
- Remoção é **soft-delete** (`st_arquivo='I'`). Objeto físico permanece no bucket até GC posterior. Ver `docs/operacao/storage-antivirus.md` (TODO) para job de GC.

---

## 6. UI

- **`clinica/recepcao-dia.xhtml`** (enfermeiro/admin) — bloco "Anexos" no `dlgAnotacoes` por consulta
- **`clinica/minha-agenda.xhtml`** (médico) — idem em `dlgAnotacoesMA`
- **`paciente/minhas-consultas.xhtml`** (paciente) — botão "Anexar exames" só em `AGENDADA`/`CONFIRMADA`, abre `dlgExames`

Componente: `<p:fileUpload mode="advanced" auto="true" multiple="true" allowTypes="..." sizeLimit="20971520">`. Native uploader (Servlet 3.0 multipart), sem dependência de commons-fileupload.

Configuração em [`web.xml`](../../hsg-his-web/src/main/webapp/WEB-INF/web.xml):
- `<context-param>primefaces.UPLOADER=native</context-param>`
- `<multipart-config>` no `FacesServlet` com limites 20 MB por arquivo, 100 MB por request

---

## 7. Persistência

- Migration: [`V32__create_arquivo.sql`](../../infra/db/migrations/V32__create_arquivo.sql)
- Tabela: `hsg.tb_arquivo`
- Entidade JPA: [`Arquivo`](../../hsg-his-domain/src/main/java/br/com/hsg/domain/entity/Arquivo.java) com factory `registrar(dominio, ownerId, pathLogico, nome, contentType, tamanho, sha256, idResp, tpResp)`
- DAO: [`ArquivoDAO`](../../hsg-his-service/src/main/java/br/com/hsg/dao/ArquivoDAO.java) — `salvar/atualizar/buscarPorId/buscarPorPathLogico/listarPor*/listarInativosParaGc`

Schema e regras estão em `docs/dominio/modelo-arquivo.md`.

---

## 8. Configuração (env vars)

Detalhes operacionais em `docs/operacao/storage-config.md`. Em resumo:

- `APP_STORAGE_ENDPOINT` — vazio em PRD AWS (SDK usa default), `http://minio:9000` em DEV
- `APP_STORAGE_REGION`, `APP_STORAGE_ACCESS_KEY`, `APP_STORAGE_SECRET_KEY`
- `APP_STORAGE_PATH_STYLE` — `true` em DEV (MinIO), `false` em PRD (S3 virtual-host)
- `APP_STORAGE_BUCKET_{ANEXO_CLIENTE|ANEXO_CONSULTA|ANEXO_ANOTACAO|EXAME_CONSULTA}`
- `APP_STORAGE_MAX_BYTES`, `APP_STORAGE_PRESIGN_TTL_MIN`

---

## 9. Testes

- Domínio: `ArquivoTest` (16), `StorageDomainTest` (4)
- Serviço: `StoragePathResolverImplTest` (12), `StorageGuardTest` (19), `S3ObjectStorageServiceImplTest` (10), `ArquivoServiceImplTest` (25), `ArquivoDAOTest` (12), `BucketBindingTest` (5), `StoragePutResultTest` (1)
- Total módulo storage: **104 testes unitários**
- Smoke manual: docker compose up, MinIO console em `http://localhost:9001` (minioadmin/minioadmin), validar bucket criado em `mc ls local`, upload pela UI

---

## 10. Pontos abertos

- Antivírus (ClamAV sidecar) — ver `docs/operacao/storage-antivirus.md`
- Job de GC de objetos órfãos (`st_arquivo='I'` que ficou no bucket)
- Lifecycle policy por bucket em PRD (retenção legal: exames 5 anos, anotações 20 anos)
- Encryption at rest: SSE-S3 default em PRD (config server-side AWS). Em DEV MinIO sem KMS, não habilitado.
- Versioning de bucket: ON em DEV (já) e PRD (a configurar)
- Migração para Azure Blob: criar `AzureBlobObjectStorageServiceImpl` consumindo mesmo `StoragePathResolver`
