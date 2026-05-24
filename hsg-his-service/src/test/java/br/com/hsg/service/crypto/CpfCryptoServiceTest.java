package br.com.hsg.service.crypto;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CpfCryptoServiceTest {

    private CpfCryptoService service;

    @Before
    public void setUp() {
        service = new CpfCryptoService();
    }

    @Test
    public void hash_deveSerDeterministicoEHexadecimal() {
        String h1 = service.hash("12345678900");
        String h2 = service.hash("12345678900");
        assertEquals(h1, h2);
        assertEquals(64, h1.length());
    }

    @Test
    public void hash_deveDiferirParaCpfsDistintos() {
        assertNotEquals(service.hash("12345678900"), service.hash("00987654321"));
    }

    @Test
    public void encrypt_naoDeveRetornarTextoPlano() {
        String cpf = "12345678900";
        String enc = service.encrypt(cpf);
        assertNotNull(enc);
        assertNotEquals(cpf, enc);
    }

    @Test
    public void encrypt_deveGerarPayloadsDiferentesPorIvAleatorio() {
        assertNotEquals(service.encrypt("12345678900"), service.encrypt("12345678900"));
    }
}
