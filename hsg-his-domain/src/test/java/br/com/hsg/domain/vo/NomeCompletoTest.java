package br.com.hsg.domain.vo;

import lombok.Getter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import static org.junit.Assert.assertEquals;

public class NomeCompletoTest {

    public void deveRetornarNomeCompleto(){
        NomeCompleto nomeCompleto = new NomeCompleto("Cláudio", "Araújo");

        assertEquals("Cláudio Araújo", nomeCompleto.getNomeCompleto());
    }
}