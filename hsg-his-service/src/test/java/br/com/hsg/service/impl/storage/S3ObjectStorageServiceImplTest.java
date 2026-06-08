package br.com.hsg.service.impl.storage;

import br.com.hsg.service.facade.storage.BucketBinding;
import br.com.hsg.service.facade.storage.StoragePathResolver;
import br.com.hsg.service.facade.storage.StoragePutResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class S3ObjectStorageServiceImplTest {

    private StoragePathResolver resolver;
    private S3Client    s3;
    private S3Presigner presigner;
    private S3ObjectStorageServiceImpl service;

    private static final String PATH = "/anexos/consulta/45/2026/06/abc.pdf";
    private static final String BUCKET = "bkt-cons";
    private static final String KEY    = "45/2026/06/abc.pdf";

    @Before
    public void setUp() {
        resolver  = mock(StoragePathResolver.class);
        s3        = mock(S3Client.class);
        presigner = mock(S3Presigner.class);
        service   = new S3ObjectStorageServiceImpl(resolver, s3, presigner);

        when(resolver.resolve(PATH)).thenReturn(new BucketBinding(BUCKET, KEY));
    }

    @Test
    public void put_deveDelegarPraS3ComBucketKeyEContentType() {
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().eTag("etag-123").build());

        InputStream stream = new ByteArrayInputStream(new byte[]{1,2,3,4});
        StoragePutResult r = service.put(PATH, stream, 4L, "application/pdf");

        ArgumentCaptor<PutObjectRequest> cap = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3).putObject(cap.capture(), any(RequestBody.class));
        PutObjectRequest req = cap.getValue();
        assertEquals(BUCKET, req.bucket());
        assertEquals(KEY,    req.key());
        assertEquals("application/pdf", req.contentType());
        assertEquals(Long.valueOf(4L),  req.contentLength());
        assertEquals(PATH,              r.getPathLogico());
        assertEquals("application/pdf", r.getContentType());
        assertEquals(4L,                r.getSizeBytes());
        assertEquals("etag-123",        r.getEtag());
    }

    @Test(expected = IllegalArgumentException.class)
    public void put_deveLancarSeStreamNull() {
        service.put(PATH, null, 4L, "application/pdf");
    }

    @Test(expected = IllegalArgumentException.class)
    public void put_deveLancarSeSizeNaoPositivo() {
        service.put(PATH, new ByteArrayInputStream(new byte[0]), 0L, "application/pdf");
    }

    @Test
    public void get_deveBaixarBytesDoS3() throws Exception {
        byte[] payload = new byte[]{10, 20, 30};
        ResponseInputStream<GetObjectResponse> ris = new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                software.amazon.awssdk.http.AbortableInputStream.create(new ByteArrayInputStream(payload)));
        when(s3.getObject(any(GetObjectRequest.class))).thenReturn(ris);

        byte[] read = service.get(PATH);

        assertArrayEquals(payload, read);
        ArgumentCaptor<GetObjectRequest> cap = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3).getObject(cap.capture());
        assertEquals(BUCKET, cap.getValue().bucket());
        assertEquals(KEY,    cap.getValue().key());
    }

    @Test
    public void presignedGet_deveRetornarUrlGeradaPeloPresigner() throws Exception {
        URL url = new URL("https://signed.example/abc");
        PresignedGetObjectRequest pres = mock(PresignedGetObjectRequest.class);
        when(pres.url()).thenReturn(url);
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(pres);

        URL out = service.presignedGet(PATH, Duration.ofMinutes(15));

        assertSame(url, out);
        ArgumentCaptor<GetObjectPresignRequest> cap = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(presigner).presignGetObject(cap.capture());
        assertEquals(Duration.ofMinutes(15), cap.getValue().signatureDuration());
        assertEquals(BUCKET, cap.getValue().getObjectRequest().bucket());
        assertEquals(KEY,    cap.getValue().getObjectRequest().key());
    }

    @Test(expected = IllegalArgumentException.class)
    public void presignedGet_deveLancarSeTtlNull() {
        service.presignedGet(PATH, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void presignedGet_deveLancarSeTtlZero() {
        service.presignedGet(PATH, Duration.ZERO);
    }

    @Test
    public void delete_deveDelegarPraS3() {
        service.delete(PATH);
        ArgumentCaptor<DeleteObjectRequest> cap = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3).deleteObject(cap.capture());
        assertEquals(BUCKET, cap.getValue().bucket());
        assertEquals(KEY,    cap.getValue().key());
    }

    @Test
    public void exists_true() {
        when(s3.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());
        assertTrue(service.exists(PATH));
    }

    @Test
    public void exists_false_quandoNoSuchKey() {
        when(s3.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("missing").build());
        assertFalse(service.exists(PATH));
    }
}
