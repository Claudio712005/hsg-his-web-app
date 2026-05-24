package br.com.hsg.domain;

import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.vo.DataNascimento;
import br.com.hsg.domain.vo.Email;
import br.com.hsg.domain.vo.NomeCompleto;
import br.com.hsg.domain.vo.Telefone;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class PacienteTest {

    private Paciente criarPacienteValido() {
        return Paciente.criar(
                new NomeCompleto("Cláudio", "Araújo"),
                new Email("teste@email.com"),
                new DataNascimento(LocalDate.of(2000, 1, 1)),
                new Telefone("11999999999")
        );
    }

    @Test
    public void deveCriarPacienteQuandoDadosValidos(){
        Paciente paciente = criarPacienteValido();

        assertNotNull(paciente);
        assertTrue(paciente.isAtivo());
        assertEquals(IndicativoStatus.A, paciente.getStatus());
        assertNotNull(paciente.getDataCadastro());
    }

    @Test
    public void deveInativarPaciente(){
        Paciente paciente = criarPacienteValido();

        paciente.inativar();

        assertFalse(paciente.isAtivo());
        assertEquals(IndicativoStatus.I, paciente.getStatus());
    }

    @Test
    public void deveAtivarPaciente(){
        Paciente paciente = criarPacienteValido();

        paciente.inativar();
        paciente.ativar();

        assertTrue(paciente.isAtivo());
        assertEquals(IndicativoStatus.A, paciente.getStatus());
    }

    @Test
    public void deveAtualizarDadosPessoais(){
        Paciente paciente = criarPacienteValido();

        paciente.atualizarDadosPessoais(new NomeCompleto("Novo", "Nome"));

        assertEquals("Novo Nome", paciente.getNomeCompleto());
        assertNotNull(paciente.getDataUltimaAtualizacao());
    }

    @Test
    public void deveAtualizarContato(){
        Paciente paciente = criarPacienteValido();

        paciente.atualizarContato(
                new Email("novo@email.com"),
                new Telefone("11888888888")
        );

        assertEquals("novo@email.com", paciente.getEmail());
        assertEquals("11888888888", paciente.getTelefone());
        assertNotNull(paciente.getDataUltimaAtualizacao());
    }

    @Test
    public void deveDefinirCpf(){
        Paciente paciente = criarPacienteValido();

        paciente.definirCpf("hash123", "enc123");

        assertNotNull(paciente);
    }

    @Test
    public void deveDefinirRg(){
        Paciente paciente = criarPacienteValido();

        paciente.definirRg("rgEnc");

        assertNotNull(paciente);
    }

    @Test
    public void deveRetornarDadosCorretos(){
        Paciente paciente = criarPacienteValido();

        assertEquals("Cláudio Araújo", paciente.getNomeCompleto());
        assertEquals("teste@email.com", paciente.getEmail());
        assertEquals(LocalDate.of(2000, 1, 1), paciente.getDataNascimento());
        assertEquals("11999999999", paciente.getTelefone());
    }
}