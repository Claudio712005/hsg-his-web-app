package br.com.hsg.service.facade.admin;

import br.com.hsg.domain.entity.EnvioConviteHistorico;
import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.domain.enums.CategoriaCoren;
import br.com.hsg.domain.enums.StatusPreCadastro;
import br.com.hsg.domain.enums.TipoProfissional;

import javax.ejb.Local;
import java.util.List;

@Local
public interface PreCadastroServiceFacade {

    PreCadastroProfissional criarParaMedico(
            String nome,
            String email,
            String cpf,
            String crm,
            String ufCrm,
            String especialidade,
            Long idAdminCriador
    );

    PreCadastroProfissional criarParaEnfermeiro(
            String nome,
            String email,
            String cpf,
            String coren,
            String ufCoren,
            CategoriaCoren categoriaCoren,
            Long idAdminCriador
    );

    void enviarConvite(Long preCadastroId, Long idAdmin, String nomeAdmin);

    void concluirCadastro(String token);

    List<EnvioConviteHistorico> buscarHistorico(Long preCadastroId);

    PreCadastroProfissional buscarPorToken(String token);

    PreCadastroProfissional buscarPorId(Long id);

    List<PreCadastroProfissional> listarTodos(int inicio, int tamanho);

    List<PreCadastroProfissional> listarPorStatus(StatusPreCadastro status, int inicio, int tamanho);

    List<PreCadastroProfissional> listarPorTipo(TipoProfissional tipo, int inicio, int tamanho);

    long contar();

    long contarPorStatus(StatusPreCadastro status);
}
