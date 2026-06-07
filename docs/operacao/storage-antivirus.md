# Storage — Antivírus e GC (TODO)

Documento-stub. Pipeline ainda não implementado. Capturado aqui o desenho para implementação futura.

---

## 1. Por que adiar AV

- MVP do storage entrega upload/download autenticado, whitelist de MIME e validação de magic bytes — defesas suficientes contra payload comum.
- ClamAV exige sidecar com 2+ GB RAM e ciclo de atualização de assinaturas. Não cabe no escopo do MVP.
- Vulnerabilidades reais (zero-day) não são detectadas por AV signature-based; mitigação real é separar bucket de quarentena + scan downstream.

## 2. Pipeline futuro

```
upload  →  put no bucket de "quarentena" (`hsg-<prod>-quarentena`)
              │
              ↓
       evento S3 → SQS/EventBridge
              │
              ↓
       worker AV (ClamAV ou GuardDuty Malware Protection)
              │
              ├── limpo → move pro bucket final + UPDATE st_arquivo='A'
              └── infectado → delete + alerta admin + UPDATE st_arquivo='Q' (quarantine)
```

DEV: usar ClamAV em container separado consumindo bucket via webhook MinIO.

## 3. Job de GC de órfãos (relacionado)

Arquivos `st_arquivo='I'` (soft-deletados) ainda têm objeto físico no bucket. Job diário deve:

1. Ler `ArquivoDAO.listarInativosParaGc(500)` ordenado por `dt_upload ASC`
2. Para cada: `objectStorageService.delete(pathLogico)`
3. Após delete físico OK, remover linha do DB
4. Log de auditoria

Implementação esperada:

```java
@Singleton
@Startup
public class StorageGcScheduler {
    @EJB private ArquivoDAO arquivoDAO;
    @EJB private ObjectStorageService storage;

    @Schedule(hour = "2", minute = "0", persistent = false)
    public void rodarGc() {
        // ...
    }
}
```

## 4. Versioning e recuperação

Mesmo após GC, o versioning do bucket guarda a versão "deletada" por X dias (lifecycle policy). Permite resgate manual em incidente.

## 5. Métricas e alertas

A implementar:
- Contador de arquivos por domínio e por status (Prometheus)
- Alerta se taxa de uploads infectados > limiar
- Alerta se GC atrasar (backlog crescente)
