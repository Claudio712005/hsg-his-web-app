package br.com.hsg.service.crypto;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CarteirinhaCryptoServiceTest {

    private CarteirinhaCryptoService service;

    @Before
    public void setUp() {
        service = new CarteirinhaCryptoService();
    }

    @Test
    public void hash_deveSerDeterministicoEHexadecimal() {
        String h1 = service.hash("123456789");
        String h2 = service.hash("123456789");
        assertEquals(h1, h2);
        assertEquals(64, h1.length());
    }

    @Test
    public void encryptDecrypt_deveRetornarValorOriginal() {
        String original = "987654321";
        String enc = service.encrypt(original);
        assertNotEquals(original, enc);
        assertEquals(original, service.decrypt(enc));
    }

    @Test
    public void mascarar_deveExibirApenasUltimosQuatroDigitos() {
        assertEquals("****6789", service.mascarar("123456789"));
    }

    @Test
    public void mascarar_deveOcultarTudoSeQuatroOuMenosDigitos() {
        assertEquals("****", service.mascarar("12"));
    }

    @Test
    public void mascarar_deveRetornarNuloSeEntradaNula() {
        assertNull(service.mascarar(null));
    }

    @Test(expected = RuntimeException.class)
    public void decrypt_deveLancarExcecaoSePayloadInvalido() {
        service.decrypt("nao-e-base64-valido-aes");
    }
}
