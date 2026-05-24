package br.com.hsg.web.bean.admin;

import br.com.hsg.domain.entity.EnvioConviteHistorico;
import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.domain.enums.CategoriaCoren;
import br.com.hsg.domain.enums.EspecialidadeMedica;
import br.com.hsg.domain.enums.Estado;
import br.com.hsg.domain.enums.StatusPreCadastro;
import br.com.hsg.domain.enums.TipoProfissional;
import br.com.hsg.service.facade.admin.PreCadastroServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.form.PreCadastroFormDTO;
import br.com.hsg.web.util.JSFUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ViewScoped
@Named("preCadastroBean")
public class PreCadastroBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private PreCadastroServiceFacade preCadastroService;
    @Inject private BeanSessao               beanSessao;

    private PreCadastroFormDTO              form;
    private List<PreCadastroProfissional>   lista;
    private PreCadastroProfissional         selecionado;
    private List<EnvioConviteHistorico>     historico;

    @PostConstruct
    public void init() {
        garantirAdmin();
        form = new PreCadastroFormDTO();
        carregarLista();
    }

    public void salvar() {
        garantirAdmin();
        try {
            Long idAdmin = beanSessao.getAdmin().getId();

            if (form.isMedico()) {
                preCadastroService.criarParaMedico(
                        form.getNome(),
                        form.getEmailPessoal(),
                        form.getCpf(),
                        form.getCrm(),
                        form.getUfCrm() != null ? form.getUfCrm().getSigla() : null,
                        form.getEspecialidade() != null ? form.getEspecialidade().getDescricao() : null,
                        idAdmin);
            } else if (form.isEnfermeiro()) {
                preCadastroService.criarParaEnfermeiro(
                        form.getNome(),
                        form.getEmailPessoal(),
                        form.getCpf(),
                        form.getCoren(),
                        form.getUfCoren() != null ? form.getUfCoren().getSigla() : null,
                        form.getCategoriaCoren(),
                        idAdmin);
            } else {
                JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_WARN,
                        "Selecione o tipo de profissional.");
                return;
            }

            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_INFO,
                    "Pré-cadastro criado com sucesso. Use o botão de envelope para enviar o convite.");
            form.limpar();
            carregarLista();

        } catch (IllegalArgumentException | IllegalStateException e) {
            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_WARN, e.getMessage());
        }
    }

    public void enviarConvite(Long preCadastroId) {
        garantirAdmin();
        try {
            Long   idAdmin   = beanSessao.getAdmin().getId();
            String nomeAdmin = beanSessao.getAdmin().getNomeCompleto();
            preCadastroService.enviarConvite(preCadastroId, idAdmin, nomeAdmin);
            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_INFO,
                    "Convite enviado com sucesso. Link válido por 2 dias.");
            carregarLista();
            if (selecionado != null && selecionado.getId().equals(preCadastroId)) {
                selecionado = preCadastroService.buscarPorId(preCadastroId);
                historico   = preCadastroService.buscarHistorico(preCadastroId);
            }
        } catch (IllegalStateException e) {
            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_WARN, e.getMessage());
        } catch (RuntimeException e) {
            JSFUtil.adicionarMensagem(null, FacesMessage.SEVERITY_ERROR,
                    "Falha ao enviar e-mail. O erro foi registrado no histórico.");
            carregarLista();
            if (selecionado != null && selecionado.getId().equals(preCadastroId)) {
                historico = preCadastroService.buscarHistorico(preCadastroId);
            }
        }
    }

    public void prepararVisualizacao(Long preCadastroId) {
        garantirAdmin();
        selecionado = preCadastroService.buscarPorId(preCadastroId);
        historico   = selecionado != null
                ? preCadastroService.buscarHistorico(preCadastroId)
                : Collections.emptyList();
    }

    public void limparFormulario() {
        form.limpar();
    }

    public TipoProfissional[]    getTiposProfissional()   { return TipoProfissional.values(); }
    public CategoriaCoren[]      getCategoriasCoren()     { return CategoriaCoren.values(); }
    public EspecialidadeMedica[] getEspecialidades()      { return EspecialidadeMedica.values(); }
    public Estado[]              getEstados()             { return Estado.values(); }

    public long getTotalPendentes() {
        return preCadastroService.contarPorStatus(StatusPreCadastro.PENDENTE);
    }

    private void carregarLista() {
        lista = preCadastroService.listarTodos(0, 50);
    }

    private void garantirAdmin() {
        if (!beanSessao.isLogadoComoAdmin()) {
            throw new SecurityException("Acesso negado: funcionalidade restrita a administradores.");
        }
    }

    public PreCadastroFormDTO getForm()                         { return form; }
    public List<PreCadastroProfissional> getLista()             { return lista; }
    public PreCadastroProfissional getSelecionado()             { return selecionado; }
    public List<EnvioConviteHistorico> getHistorico()           { return historico; }
}
