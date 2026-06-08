package br.com.hsg.service.impl.clinica;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.Receita;
import br.com.hsg.domain.entity.ReceitaItem;
import br.com.hsg.domain.vo.Crm;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReceitaPdfBuilderTest {

    @Test
    public void build_devolveBytesPdfValidos() {
        Medico medico = mock(Medico.class);
        when(medico.getNomeCompleto()).thenReturn("Roberto Mendes");
        when(medico.getCrm()).thenReturn(new Crm("123456", "SP"));

        Paciente paciente = mock(Paciente.class);
        when(paciente.getNomeCompleto()).thenReturn("Claudio Filho");

        Consulta consulta = mock(Consulta.class);
        when(consulta.getMedico()).thenReturn(medico);
        when(consulta.getPaciente()).thenReturn(paciente);

        Receita r = Receita.emitir(consulta, medico, Arrays.asList(
                ReceitaItem.criar("Dipirona 500mg", "1 cp via oral 6/6h",
                        "Suspender se melhora", "R51", 1),
                ReceitaItem.criar("SRO", "1 sachê após cada evacuação",
                        null, null, 2)));

        byte[] pdf = ReceitaPdfBuilder.build(r);
        assertNotNull(pdf);
        assertTrue("Tamanho inesperado: " + pdf.length, pdf.length > 1000);
        assertTrue("Não começa com %PDF-",
                pdf[0] == '%' && pdf[1] == 'P' && pdf[2] == 'D' && pdf[3] == 'F' && pdf[4] == '-');
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_null_deveLancar() {
        ReceitaPdfBuilder.build(null);
    }

    @Test(expected = IllegalStateException.class)
    public void build_consultaSemPaciente_deveLancar() {
        Medico medico = mock(Medico.class);
        when(medico.getNomeCompleto()).thenReturn("Roberto");
        when(medico.getCrm()).thenReturn(new Crm("123456", "SP"));
        Consulta consulta = mock(Consulta.class);
        when(consulta.getMedico()).thenReturn(medico);
        when(consulta.getPaciente()).thenReturn(null);

        Receita r = Receita.emitir(consulta, medico,
                Collections.singletonList(ReceitaItem.criar("Dipirona", "1 cp", null, null, 1)));
        ReceitaPdfBuilder.build(r);
    }
}
