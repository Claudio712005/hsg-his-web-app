package br.com.hsg.web.dto.form;

import br.com.hsg.domain.enums.CategoriaCoren;
import br.com.hsg.domain.enums.EspecialidadeMedica;
import br.com.hsg.domain.enums.Estado;
import br.com.hsg.domain.enums.TipoProfissional;
import lombok.Data;

import java.io.Serializable;

@Data
public class PreCadastroFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private TipoProfissional tipoProfissional;

    private String nome;
    private String emailPessoal;
    private String cpf;

    private String crm;
    private Estado ufCrm;
    private EspecialidadeMedica especialidade;

    private String coren;
    private Estado ufCoren;
    private CategoriaCoren categoriaCoren;

    public void limpar() {
        tipoProfissional = null;
        nome = emailPessoal = cpf = null;
        crm = null;
        ufCrm = null;
        especialidade = null;
        coren = null;
        ufCoren = null;
        categoriaCoren = null;
    }

    public boolean isMedico() {
        return TipoProfissional.MEDICO.equals(tipoProfissional);
    }

    public boolean isEnfermeiro() {
        return TipoProfissional.ENFERMEIRO.equals(tipoProfissional);
    }
}
