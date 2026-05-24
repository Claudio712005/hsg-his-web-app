package br.com.hsg.service.facade.public_;

import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.service.dto.AtivacaoFormDTO;

import javax.ejb.Local;
import java.util.List;

@Local
public interface AtivacaoServiceFacade {

    PreCadastroProfissional validarToken(String token);

    void ativarCadastro(String token, AtivacaoFormDTO form);

    List<Especialidade> listarEspecialidadesAtivas();
}
