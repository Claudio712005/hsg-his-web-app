package br.com.hsg.service.impl.storage;

import br.com.hsg.domain.enums.StorageDomain;
import br.com.hsg.service.facade.storage.BucketBinding;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StoragePathResolverImplTest {

    private StoragePathResolverImpl resolver;

    @Before
    public void setUp() {
        Map<StorageDomain, String> buckets = new EnumMap<>(StorageDomain.class);
        buckets.put(StorageDomain.ANEXO_CLIENTE,  "bkt-cli");
        buckets.put(StorageDomain.ANEXO_CONSULTA, "bkt-cons");
        buckets.put(StorageDomain.ANEXO_ANOTACAO, "bkt-anot");
        buckets.put(StorageDomain.EXAME_CONSULTA, "bkt-exa");
        resolver = new StoragePathResolverImpl(buckets);
    }

    @Test
    public void build_deveAplicarPrefixoOwnerIdAnoMesUuidExtensao() {
        String path = resolver.buildLogicalPath(StorageDomain.ANEXO_CONSULTA, 45L, "Exame.PDF");
        assertTrue(path, path.matches(
                "^/anexos/consulta/45/\\d{4}/\\d{2}/[0-9a-f-]{36}\\.pdf$"));
    }

    @Test
    public void build_deveOmitirExtensaoSeFilenameSemPonto() {
        String path = resolver.buildLogicalPath(StorageDomain.ANEXO_CLIENTE, 7L, "arquivoSemExt");
        assertTrue(path, path.matches(
                "^/anexos/cliente/7/\\d{4}/\\d{2}/[0-9a-f-]{36}$"));
    }

    @Test
    public void build_deveOmitirExtensaoSeExtensaoInvalida() {
        String path = resolver.buildLogicalPath(StorageDomain.EXAME_CONSULTA, 9L, "x.<>!");
        assertTrue(path, path.matches("^/exames/consulta/9/\\d{4}/\\d{2}/[0-9a-f-]{36}$"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_deveLancarSeDominioNull() {
        resolver.buildLogicalPath(null, 1L, "x.pdf");
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_deveLancarSeOwnerIdInvalido() {
        resolver.buildLogicalPath(StorageDomain.ANEXO_CLIENTE, 0L, "x.pdf");
    }

    @Test
    public void resolve_deveExtrairBucketEObjectKeyCorretos() {
        BucketBinding b = resolver.resolve("/anexos/consulta/45/2026/06/abc.pdf");
        assertEquals("bkt-cons", b.getBucket());
        assertEquals("45/2026/06/abc.pdf", b.getObjectKey());
    }

    @Test
    public void resolve_deveCobrirTodosOsDominios() {
        assertEquals("bkt-cli",  resolver.resolve("/anexos/cliente/1/2026/06/x.pdf").getBucket());
        assertEquals("bkt-cons", resolver.resolve("/anexos/consulta/2/2026/06/x.pdf").getBucket());
        assertEquals("bkt-anot", resolver.resolve("/anexos/anotacao/3/2026/06/x.pdf").getBucket());
        assertEquals("bkt-exa",  resolver.resolve("/exames/consulta/4/2026/06/x.pdf").getBucket());
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolve_deveLancarSePathDesconhecido() {
        resolver.resolve("/lixo/qualquer/x.pdf");
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolve_deveLancarSePathSemObjectKey() {
        resolver.resolve("/anexos/cliente/");
    }

    @Test(expected = IllegalStateException.class)
    public void resolve_deveLancarSeBucketAusente() {
        StoragePathResolverImpl semConfig = new StoragePathResolverImpl(new EnumMap<>(StorageDomain.class));
        semConfig.resolve("/anexos/cliente/1/2026/06/x.pdf");
    }

    @Test
    public void roundtrip_buildEntaoResolveDeveDarKeySemPrefixo() {
        String path = resolver.buildLogicalPath(StorageDomain.ANEXO_ANOTACAO, 77L, "img.PNG");
        BucketBinding b = resolver.resolve(path);
        assertEquals("bkt-anot", b.getBucket());
        assertTrue(b.getObjectKey(), b.getObjectKey().startsWith("77/"));
        assertTrue(b.getObjectKey().endsWith(".png"));
    }

    @Test
    public void extrairExtensao_deveLowercaseEValidar() {
        assertEquals("pdf",  StoragePathResolverImpl.extrairExtensao("nota.PDF"));
        assertEquals("jpeg", StoragePathResolverImpl.extrairExtensao("foto.JPEG"));
        assertEquals("",     StoragePathResolverImpl.extrairExtensao("semponto"));
        assertEquals("",     StoragePathResolverImpl.extrairExtensao(".oculto"));
        assertEquals("",     StoragePathResolverImpl.extrairExtensao("vazio."));
        assertEquals("",     StoragePathResolverImpl.extrairExtensao("longa.exteeeeeensa"));
        assertEquals("",     StoragePathResolverImpl.extrairExtensao(null));
    }
}
