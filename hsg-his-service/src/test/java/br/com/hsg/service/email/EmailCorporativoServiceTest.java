package br.com.hsg.service.email;

import br.com.hsg.dao.PreCadastroProfissionalDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EmailCorporativoServiceTest {

    @Mock private PreCadastroProfissionalDAO preCadastroDAO;
    @InjectMocks private EmailCorporativoService service;

    @Test
    public void gerar_deveUsarPrimeiroEUltimoNomeNormalizados() {
        when(preCadastroDAO.existeEmailCorporativo("joao.silva@hsg.com.br")).thenReturn(false);
        assertEquals("joao.silva@hsg.com.br", service.gerar("João Silva"));
    }

    @Test
    public void gerar_deveAdicionarSufixoQuandoEmailJaExiste() {
        when(preCadastroDAO.existeEmailCorporativo("joao.silva@hsg.com.br")).thenReturn(true);
        when(preCadastroDAO.existeEmailCorporativo("joao.silva2@hsg.com.br")).thenReturn(false);
        assertEquals("joao.silva2@hsg.com.br", service.gerar("João Silva"));
    }

    @Test
    public void gerar_deveUsarFallbackQuandoNomeNulo() {
        when(preCadastroDAO.existeEmailCorporativo("profissional@hsg.com.br")).thenReturn(false);
        assertEquals("profissional@hsg.com.br", service.gerar(null));
    }
}
