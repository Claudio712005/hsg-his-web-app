package br.com.hsg.service.impl.storage;

import br.com.hsg.service.facade.storage.BucketBinding;
import br.com.hsg.service.facade.storage.ObjectStorageService;
import br.com.hsg.service.facade.storage.StoragePathResolver;
import br.com.hsg.service.facade.storage.StoragePutResult;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.S3Presigner.Builder;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class S3ObjectStorageServiceImpl implements ObjectStorageService {

    private static final Logger LOG = Logger.getLogger(S3ObjectStorageServiceImpl.class.getName());

    static final String ENV_ENDPOINT    = "APP_STORAGE_ENDPOINT";
    static final String ENV_REGION      = "APP_STORAGE_REGION";
    static final String ENV_ACCESS_KEY  = "APP_STORAGE_ACCESS_KEY";
    static final String ENV_SECRET_KEY  = "APP_STORAGE_SECRET_KEY";
    static final String ENV_PATH_STYLE  = "APP_STORAGE_PATH_STYLE";

    @EJB
    private StoragePathResolver resolver;

    private S3Client    s3;
    private S3Presigner presigner;

    public S3ObjectStorageServiceImpl() {}

    S3ObjectStorageServiceImpl(StoragePathResolver resolver, S3Client s3, S3Presigner presigner) {
        this.resolver  = resolver;
        this.s3        = s3;
        this.presigner = presigner;
    }

    @PostConstruct
    public void init() {
        if (s3 != null && presigner != null) return;

        String endpoint   = env(ENV_ENDPOINT, null);
        String regionStr  = env(ENV_REGION, "us-east-1");
        String accessKey  = env(ENV_ACCESS_KEY, "");
        String secretKey  = env(ENV_SECRET_KEY, "");
        boolean pathStyle = "true".equalsIgnoreCase(env(ENV_PATH_STYLE, "false"));

        Region region = Region.of(regionStr);
        StaticCredentialsProvider creds = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey));

        S3Configuration s3Cfg = S3Configuration.builder()
                .pathStyleAccessEnabled(pathStyle)
                .build();

        S3ClientBuilder builder = S3Client.builder()
                .region(region)
                .credentialsProvider(creds)
                .httpClient(UrlConnectionHttpClient.builder().build())
                .serviceConfiguration(s3Cfg);

        Builder presignerBuilder = S3Presigner.builder()
                .region(region)
                .credentialsProvider(creds)
                .serviceConfiguration(s3Cfg);

        if (endpoint != null && !endpoint.isEmpty()) {
            URI uri = URI.create(endpoint);
            builder.endpointOverride(uri);
            presignerBuilder.endpointOverride(uri);
        }

        this.s3        = builder.build();
        this.presigner = presignerBuilder.build();
        LOG.log(Level.INFO, "[S3ObjectStorage] Inicializado region={0} endpoint={1} pathStyle={2}",
                new Object[]{regionStr, endpoint == null ? "(default AWS)" : endpoint, pathStyle});
    }

    @PreDestroy
    public void close() {
        try { if (presigner != null) presigner.close(); } catch (Exception ignore) {}
        try { if (s3 != null) s3.close(); } catch (Exception ignore) {}
    }

    @Override
    public StoragePutResult put(String pathLogico, InputStream stream, long size, String contentType) {
        if (stream == null) throw new IllegalArgumentException("Stream é obrigatório.");
        if (size <= 0L)     throw new IllegalArgumentException("Tamanho deve ser positivo.");
        BucketBinding b = resolver.resolve(pathLogico);
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(b.getBucket())
                .key(b.getObjectKey())
                .contentType(contentType)
                .contentLength(size)
                .build();
        PutObjectResponse resp = s3.putObject(req, RequestBody.fromInputStream(stream, size));
        return new StoragePutResult(pathLogico, size, contentType, resp.eTag());
    }

    @Override
    public byte[] get(String pathLogico) {
        BucketBinding b = resolver.resolve(pathLogico);
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(b.getBucket())
                .key(b.getObjectKey())
                .build();
        try (ResponseInputStream<?> in = s3.getObject(req);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) baos.write(buf, 0, n);
            return baos.toByteArray();
        } catch (IOException ioe) {
            throw new IllegalStateException("Falha ao ler objeto " + pathLogico, ioe);
        }
    }

    @Override
    public URL presignedGet(String pathLogico, Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("TTL deve ser positivo.");
        }
        BucketBinding b = resolver.resolve(pathLogico);
        GetObjectRequest get = GetObjectRequest.builder()
                .bucket(b.getBucket())
                .key(b.getObjectKey())
                .build();
        GetObjectPresignRequest preq = GetObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .getObjectRequest(get)
                .build();
        PresignedGetObjectRequest pres = presigner.presignGetObject(preq);
        return pres.url();
    }

    @Override
    public void delete(String pathLogico) {
        BucketBinding b = resolver.resolve(pathLogico);
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(b.getBucket())
                .key(b.getObjectKey())
                .build());
    }

    @Override
    public boolean exists(String pathLogico) {
        BucketBinding b = resolver.resolve(pathLogico);
        try {
            s3.headObject(HeadObjectRequest.builder()
                    .bucket(b.getBucket())
                    .key(b.getObjectKey())
                    .build());
            return true;
        } catch (NoSuchKeyException nsk) {
            return false;
        }
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v != null && !v.trim().isEmpty()) ? v.trim() : fallback;
    }
}
