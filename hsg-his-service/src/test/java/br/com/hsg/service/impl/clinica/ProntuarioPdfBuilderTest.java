package br.com.hsg.service.impl.clinica;

import br.com.hsg.service.dto.prontuario.AlergiaResumoDTO;
import br.com.hsg.service.dto.prontuario.AnotacaoResumoDTO;
import br.com.hsg.service.dto.prontuario.ConsultaResumoDTO;
import br.com.hsg.service.dto.prontuario.PacienteResumoDTO;
import br.com.hsg.service.dto.prontuario.ProntuarioDTO;
import br.com.hsg.service.dto.prontuario.ReceitaResumoDTO;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProntuarioPdfBuilderTest {

    @Test
    public void build_caminhoCompleto_geraBytesPdfValidos() {
        ProntuarioDTO dto = new ProntuarioDTO();

        PacienteResumoDTO p = new PacienteResumoDTO();
        p.setId(100L);
        p.setNomeCompleto("Claudio Filho");
        p.setDataNascimento(LocalDate.of(1995, 1, 1));
        p.setIdade(31);
        dto.setPaciente(p);

        AlergiaResumoDTO al = new AlergiaResumoDTO();
        al.setNome("Dipirona");
        al.setTipo("MEDICAMENTOSA");
        al.setGravidade("GRAVE");
        al.setStatus("APROVADA");
        al.setReacao("Urticária generalizada");
        List<AlergiaResumoDTO> alergias = new ArrayList<>();
        alergias.add(al);
        dto.setAlergias(alergias);

        ConsultaResumoDTO c = new ConsultaResumoDTO();
        c.setId(3L);
        c.setDataConsulta(LocalDateTime.of(2026, 5, 26, 9, 0));
        c.setStatus("REALIZADA");
        c.setMedicoNome("Roberto Mendes");
        c.setMedicoCrm("CRM-SP 123456");
        c.setEspecialidade("Clínica Médica");
        c.setObservacaoClinica("Gastroenterite leve.");

        AnotacaoResumoDTO an = new AnotacaoResumoDTO();
        an.setTitulo("Conduta clínica");
        an.setDescricao("Hidratação oral, dieta branda.");
        c.getAnotacoes().add(an);

        ReceitaResumoDTO r = new ReceitaResumoDTO();
        r.setId(1L);
        r.setDataEmissao(LocalDateTime.of(2026, 5, 26, 9, 25));
        r.setAtiva(true);
        ReceitaResumoDTO.ItemDTO it = new ReceitaResumoDTO.ItemDTO();
        it.setMedicamento("Dipirona 500mg");
        it.setPosologia("1 cp 6/6h");
        r.getItens().add(it);
        c.setReceitaAtiva(r);

        dto.getConsultas().add(c);

        byte[] pdf = ProntuarioPdfBuilder.build(dto);
        assertNotNull(pdf);
        assertTrue("Tamanho inesperado: " + pdf.length, pdf.length > 1000);
        assertTrue("Não começa com %PDF-",
                pdf[0] == '%' && pdf[1] == 'P' && pdf[2] == 'D' && pdf[3] == 'F' && pdf[4] == '-');
    }

    @Test
    public void build_semConsultasNemAlergias_aindaGeraPdf() {
        ProntuarioDTO dto = new ProntuarioDTO();
        PacienteResumoDTO p = new PacienteResumoDTO();
        p.setNomeCompleto("Maria Sem Histórico");
        dto.setPaciente(p);
        byte[] pdf = ProntuarioPdfBuilder.build(dto);
        assertNotNull(pdf);
        assertTrue(pdf.length > 500);
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_dtoNull_deveLancar() {
        ProntuarioPdfBuilder.build(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_semPaciente_deveLancar() {
        ProntuarioDTO dto = new ProntuarioDTO();
        ProntuarioPdfBuilder.build(dto);
    }
}
