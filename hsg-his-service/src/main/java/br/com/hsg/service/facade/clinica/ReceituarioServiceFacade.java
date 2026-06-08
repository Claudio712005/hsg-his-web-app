package br.com.hsg.service.facade.clinica;

import br.com.hsg.domain.entity.Receita;
import br.com.hsg.domain.enums.TipoResponsavel;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ReceituarioServiceFacade {

    final class ItemDTO {
        public final String medicamento;
        public final String posologia;
        public final String observacao;
        public final String cid10;

        public ItemDTO(String medicamento, String posologia, String observacao, String cid10) {
            this.medicamento = medicamento;
            this.posologia   = posologia;
            this.observacao  = observacao;
            this.cid10       = cid10;
        }
    }

    Receita emitir(Long idConsulta, Long idMedico, List<ItemDTO> itens);

    Receita buscarPorConsulta(Long idConsulta);

    Receita buscarParaPdf(Long idConsulta, Long idSolicitante, TipoResponsavel tipoSolicitante);

    void excluir(Long idConsulta, Long idMedico);
}
