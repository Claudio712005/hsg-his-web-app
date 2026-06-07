package br.com.hsg.service.facade.storage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BucketBindingTest {

    @Test
    public void deveExporBucketEKeyExatos() {
        BucketBinding b = new BucketBinding("hsg-anexos-consulta", "45/2026/06/abc.pdf");
        assertEquals("hsg-anexos-consulta", b.getBucket());
        assertEquals("45/2026/06/abc.pdf", b.getObjectKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarSeBucketVazio()    { new BucketBinding("  ", "k"); }
    @Test(expected = IllegalArgumentException.class)
    public void deveLancarSeBucketNull()     { new BucketBinding(null, "k"); }
    @Test(expected = IllegalArgumentException.class)
    public void deveLancarSeObjectKeyVazio() { new BucketBinding("b", "  "); }
    @Test(expected = IllegalArgumentException.class)
    public void deveLancarSeObjectKeyNull()  { new BucketBinding("b", null); }
}
