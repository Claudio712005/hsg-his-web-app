package br.com.hsg.service.facade.storage;

import br.com.hsg.domain.entity.Arquivo;
import br.com.hsg.domain.enums.TipoResponsavel;

import javax.ejb.Local;
import java.net.URL;
import java.util.List;

@Local
public interface ArquivoServiceFacade {

    Arquivo anexarEmConsulta(Long idConsulta, byte[] payload, String contentType, String filename,
                              Long idResponsavel, TipoResponsavel tipoResponsavel);

    Arquivo anexarExameEmConsulta(Long idConsulta, byte[] payload, String contentType, String filename,
                                    Long idResponsavel, TipoResponsavel tipoResponsavel);

    Arquivo anexarEmAnotacao(Long idAnotacao, byte[] payload, String contentType, String filename,
                              Long idResponsavel, TipoResponsavel tipoResponsavel);

    List<Arquivo> listarPorConsulta(Long idConsulta);

    List<Arquivo> listarPorAnotacao(Long idAnotacao);

    URL urlDownload(Long idArquivo, Long idSolicitante, TipoResponsavel tipoSolicitante);

    byte[] download(Long idArquivo, Long idSolicitante, TipoResponsavel tipoSolicitante);

    br.com.hsg.domain.entity.Arquivo metadados(Long idArquivo, Long idSolicitante, TipoResponsavel tipoSolicitante);

    void remover(Long idArquivo, Long idSolicitante, TipoResponsavel tipoSolicitante);
}
