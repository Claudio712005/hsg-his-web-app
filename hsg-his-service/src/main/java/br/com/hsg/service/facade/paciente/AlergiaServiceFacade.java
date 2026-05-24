package br.com.hsg.service.facade.paciente;

import br.com.hsg.domain.entity.Alergia;
import br.com.hsg.domain.entity.AlergiaHistorico;
import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;

import javax.ejb.Local;
import java.time.LocalDateTime;
import java.util.List;

@Local
public interface AlergiaServiceFacade {

    void informarAlergia(
            Long pacienteId,
            Long idCadastrador,
            String nome,
            String descricao,
            TipoAlergia tipoAlergia,
            GravidadeAlergia gravidadeAlergia,
            String reacao,
            LocalDateTime dataUltimaReacao,
            String observacao
    );

    void excluirAlergia(Long alergiaId, Long idUsuario);

    List<Alergia> listarPorPaciente(Long pacienteId, int inicio, int tamanho);

    long contarPorPaciente(Long pacienteId);

    Alergia buscarPorId(Long alergiaId);

    List<AlergiaHistorico> buscarHistorico(Long alergiaId);

    void aprovarAlergia(Long alergiaId, Long idAprovador, String observacao);

    void rejeitarAlergia(Long alergiaId, Long idAprovador, String observacao);

    List<Alergia> listarParaAprovacao(int inicio, int tamanho);

    long contarParaAprovacao();

    List<Alergia> listarParaAprovacao(int inicio, int tamanho,
                                      String filtroPaciente,
                                      TipoAlergia filtroTipo,
                                      GravidadeAlergia filtroGravidade);

    List<Alergia> listarParaAprovacao(int inicio, int tamanho,
                                      String filtroPaciente,
                                      TipoAlergia filtroTipo,
                                      GravidadeAlergia filtroGravidade,
                                      String sortField,
                                      boolean ascending);

    long contarParaAprovacao(String filtroPaciente,
                             TipoAlergia filtroTipo,
                             GravidadeAlergia filtroGravidade);

    List<Alergia> listarHistorico(int inicio, int tamanho,
                                  String filtroPaciente,
                                  TipoAlergia filtroTipo,
                                  GravidadeAlergia filtroGravidade,
                                  StatusAlergia filtroStatus,
                                  String sortField,
                                  boolean ascending);

    long contarHistorico(String filtroPaciente,
                         TipoAlergia filtroTipo,
                         GravidadeAlergia filtroGravidade,
                         StatusAlergia filtroStatus);
}
