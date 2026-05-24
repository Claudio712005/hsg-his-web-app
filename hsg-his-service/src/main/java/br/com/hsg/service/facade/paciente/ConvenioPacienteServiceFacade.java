package br.com.hsg.service.facade.paciente;

import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.domain.enums.TipoTitularidade;

import javax.ejb.Local;
import java.time.LocalDate;
import java.util.List;

@Local
public interface ConvenioPacienteServiceFacade {

    PacienteConvenio buscarConvenioAtivo(Long idPaciente);

    List<PacienteConvenio> listarHistorico(Long idPaciente);

    List<SolicitacaoConvenio> listarSolicitacoes(Long idPaciente);

    boolean possuiSolicitacaoPendente(Long idPaciente);

    List<Convenio> listarConveniosAtivos();

    List<PlanoConvenio> listarPlanosAtivosPorConvenio(Long idConvenio);

    PlanoConvenio buscarPlano(Long idPlano);

    List<RegraCobertura> listarRegrasDoPlano(Long idPlano);

    void solicitarConvenio(Long idPaciente, Long idPlano, String numeroCarteirinha,
                           LocalDate dataValidade, TipoTitularidade tipoTitularidade, String motivo);

    void cancelarSolicitacao(Long idSolicitacao, Long idPaciente);
}
