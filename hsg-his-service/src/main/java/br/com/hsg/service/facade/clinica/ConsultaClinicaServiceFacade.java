package br.com.hsg.service.facade.clinica;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.ConsultaHistorico;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.TipoResponsavel;

import javax.ejb.Local;
import java.time.LocalDate;
import java.util.List;

@Local
public interface ConsultaClinicaServiceFacade {

    List<Consulta> listarConsultasDoDia(LocalDate dia, Long idMedicoFiltro);

    List<Consulta> listarConsultasDoDiaMedico(Long idMedico, LocalDate dia);

    List<Consulta> listarConsultasPorPeriodo(LocalDate inicio, LocalDate fim,
                                              Long idMedicoFiltro, StatusConsulta status,
                                              String termoPaciente, Long idEspecialidade);

    List<Consulta> listarConsultasMedicoPorPeriodo(Long idMedico, LocalDate inicio, LocalDate fim,
                                                     StatusConsulta status, String termoPaciente);

    void confirmarChegada(Long idConsulta, Long idResponsavel, TipoResponsavel tipoResponsavel);

    void marcarRealizadaComObservacao(Long idConsulta, Long idMedico, String observacaoClinica);

    void marcarFaltaPelaClinica(Long idConsulta, Long idResponsavel, TipoResponsavel tipoResponsavel);

    void cancelarPelaClinica(Long idConsulta, Long idResponsavel, TipoResponsavel tipoResponsavel,
                              String motivo);

    List<ConsultaHistorico> historicoPorConsulta(Long idConsulta);

    br.com.hsg.domain.entity.ConsultaAnotacao adicionarAnotacao(Long idConsulta, String titulo,
                                                                  String descricao, Long idResponsavel,
                                                                  TipoResponsavel tipoResponsavel);

    List<br.com.hsg.domain.entity.ConsultaAnotacao> listarAnotacoes(Long idConsulta);
}
