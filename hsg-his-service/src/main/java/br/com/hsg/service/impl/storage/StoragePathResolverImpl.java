package br.com.hsg.service.impl.storage;

import br.com.hsg.domain.enums.StorageDomain;
import br.com.hsg.service.facade.storage.BucketBinding;
import br.com.hsg.service.facade.storage.StoragePathResolver;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Singleton
@Startup
public class StoragePathResolverImpl implements StoragePathResolver {

    static final String ENV_BUCKET_ANEXO_CLIENTE  = "APP_STORAGE_BUCKET_ANEXO_CLIENTE";
    static final String ENV_BUCKET_ANEXO_CONSULTA = "APP_STORAGE_BUCKET_ANEXO_CONSULTA";
    static final String ENV_BUCKET_ANEXO_ANOTACAO = "APP_STORAGE_BUCKET_ANEXO_ANOTACAO";
    static final String ENV_BUCKET_EXAME_CONSULTA = "APP_STORAGE_BUCKET_EXAME_CONSULTA";

    private static final DateTimeFormatter FMT_ANOMES = DateTimeFormatter.ofPattern("yyyy/MM");

    private final Map<StorageDomain, String> bucketByDomain = new EnumMap<>(StorageDomain.class);

    public StoragePathResolverImpl() {}

    public StoragePathResolverImpl(Map<StorageDomain, String> buckets) {
        if (buckets != null) {
            this.bucketByDomain.putAll(buckets);
        }
    }

    @PostConstruct
    public void carregar() {
        if (!bucketByDomain.isEmpty()) return;
        bucketByDomain.put(StorageDomain.ANEXO_CLIENTE,  env(ENV_BUCKET_ANEXO_CLIENTE,  "hsg-anexos-cliente"));
        bucketByDomain.put(StorageDomain.ANEXO_CONSULTA, env(ENV_BUCKET_ANEXO_CONSULTA, "hsg-anexos-consulta"));
        bucketByDomain.put(StorageDomain.ANEXO_ANOTACAO, env(ENV_BUCKET_ANEXO_ANOTACAO, "hsg-anexos-anotacao"));
        bucketByDomain.put(StorageDomain.EXAME_CONSULTA, env(ENV_BUCKET_EXAME_CONSULTA, "hsg-exames-consulta"));
    }

    @Override
    public String buildLogicalPath(StorageDomain dominio, long ownerId, String filename) {
        if (dominio == null) {
            throw new IllegalArgumentException("Domínio é obrigatório.");
        }
        if (ownerId <= 0L) {
            throw new IllegalArgumentException("ownerId deve ser positivo.");
        }
        String ext = extrairExtensao(filename);
        String mes = LocalDate.now().format(FMT_ANOMES);
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder()
                .append(dominio.getPrefixoLogico())
                .append('/').append(ownerId)
                .append('/').append(mes)
                .append('/').append(uuid);
        if (!ext.isEmpty()) {
            sb.append('.').append(ext);
        }
        return sb.toString();
    }

    @Override
    public BucketBinding resolve(String pathLogico) {
        StorageDomain dominio = StorageDomain.pelaPrefixoDoPathLogico(pathLogico);
        String bucket = bucketByDomain.get(dominio);
        if (bucket == null || bucket.isEmpty()) {
            throw new IllegalStateException("Bucket não configurado para domínio " + dominio);
        }
        String prefixoComBarra = dominio.getPrefixoLogico() + "/";
        String objectKey = pathLogico.substring(prefixoComBarra.length());
        if (objectKey.isEmpty()) {
            throw new IllegalArgumentException("Path lógico sem objectKey: " + pathLogico);
        }
        return new BucketBinding(bucket, objectKey);
    }

    static String extrairExtensao(String filename) {
        if (filename == null) return "";
        String f = filename.trim();
        int p = f.lastIndexOf('.');
        if (p <= 0 || p == f.length() - 1) return "";
        String ext = f.substring(p + 1).toLowerCase(Locale.ROOT);
        if (!ext.matches("[a-z0-9]{1,8}")) return "";
        return ext;
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v != null && !v.trim().isEmpty()) ? v.trim() : fallback;
    }
}
