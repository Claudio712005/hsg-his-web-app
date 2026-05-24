package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.entity.Endereco;
import br.com.hsg.domain.enums.Estado;
import br.com.hsg.domain.enums.TipoCancelador;
import br.com.hsg.domain.enums.TipoSanguineoEnum;
import br.com.hsg.domain.enums.TipoSolicitacao;
import br.com.hsg.service.facade.paciente.SolicitacaoAtualizacaoServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.form.AtualizacaoCadastralDTO;
import br.com.hsg.web.dto.form.AtualizacaoClinicaDTO;
import br.com.hsg.web.dto.form.AtualizacaoEnderecoDTO;
import br.com.hsg.web.dto.response.PacienteResponseDTO;
import br.com.hsg.web.dto.response.SolicitacaoAtualizacaoDTO;
import br.com.hsg.web.model.HistoricoLazyModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ViewScoped
@Named("atualizarDadosPacienteBean")
@Slf4j
public class AtualizarDadosPacienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private SolicitacaoAtualizacaoServiceFacade solicitacaoService;

    @Inject
    private BeanSessao beanSessao;

    @Getter
    private AtualizacaoCadastralDTO formCadastral;
    @Getter
    private AtualizacaoEnderecoDTO  formEndereco;
    @Getter
    private AtualizacaoClinicaDTO   formClinico;

    private String snpPrimeiroNome;
    private String snpSobrenome;
    private String snpEmail;
    private String snpTelefone;

    private String snpLogradouro;
    private String snpNumero;
    private String snpComplemento;
    private String snpBairro;
    private String snpCidade;
    private Estado snpEstado;
    private String snpCep;

    private Double snpPeso;
    private Double snpAltura;
    private String snpTipoSanguineo;

    private Long   idSolicitacaoParaCancelar;

    @Getter
    private HistoricoLazyModel historicoCadastralModel;
    @Getter
    private HistoricoLazyModel historicoClinicoModel;

    @Getter
    private List<TipoSanguineoEnum> tiposSanguineos;
    @Getter
    private List<Estado>            estados;

    @Getter
    @Setter
    private SolicitacaoAtualizacaoDTO solicitacaoVisualizacao;

    @Getter
    @Setter
    private String motivoCancelamento;

    @Getter
    @Setter
    private Integer activeTabIndex = 0;

    @PostConstruct
    public void init() {
        formCadastral   = new AtualizacaoCadastralDTO();
        formEndereco    = new AtualizacaoEnderecoDTO();
        formClinico     = new AtualizacaoClinicaDTO();
        tiposSanguineos = Arrays.asList(TipoSanguineoEnum.values());
        estados         = Arrays.asList(Estado.values());

        PacienteResponseDTO paciente = beanSessao.getPaciente();
        Long pacienteId = paciente != null ? paciente.getId() : null;
        log.debug("Inicializando AtualizarDadosPacienteBean para pacienteId={}", pacienteId);

        preencherFormCadastral(paciente);
        preencherFormEndereco(pacienteId);
        preencherFormClinico(paciente);

        capturarSnapshotCadastral();
        capturarSnapshotEndereco();
        capturarSnapshotClinico();

        List<TipoSolicitacao> tiposCadastrais = Arrays.asList(TipoSolicitacao.CADASTRAL, TipoSolicitacao.ENDERECO);
        List<TipoSolicitacao> tiposClinico    = Collections.singletonList(TipoSolicitacao.CLINICO);

        historicoCadastralModel = new HistoricoLazyModel(solicitacaoService, pacienteId, tiposCadastrais);
        historicoClinicoModel   = new HistoricoLazyModel(solicitacaoService, pacienteId, tiposClinico);
        log.debug("Bean inicializado com modelos de historico cadastrados para pacienteId={}", pacienteId);
    }

    public boolean isFormCadastralAlterado() {
        return !Objects.equals(formCadastral.getPrimeiroNome(), snpPrimeiroNome)
            || !Objects.equals(formCadastral.getSobrenome(),    snpSobrenome)
            || !Objects.equals(formCadastral.getEmail(),        snpEmail)
            || !Objects.equals(formCadastral.getTelefone(),     snpTelefone);
    }

    public boolean isFormEnderecoAlterado() {
        return !Objects.equals(formEndereco.getLogradouro(),  snpLogradouro)
            || !Objects.equals(formEndereco.getNumero(),      snpNumero)
            || !Objects.equals(formEndereco.getComplemento(), snpComplemento)
            || !Objects.equals(formEndereco.getBairro(),      snpBairro)
            || !Objects.equals(formEndereco.getCidade(),      snpCidade)
            || !Objects.equals(formEndereco.getEstado(),      snpEstado)
            || !Objects.equals(formEndereco.getCep(),         snpCep);
    }

    public boolean isFormClinicoAlterado() {
        return !Objects.equals(formClinico.getPeso(),          snpPeso)
            || !Objects.equals(formClinico.getAltura(),        snpAltura)
            || !Objects.equals(formClinico.getTipoSanguineo(), snpTipoSanguineo);
    }

    public void solicitarAtualizacaoCadastral() {
        try {
            Long pacienteId = pacienteId();
            log.info("Solicitando atualizacao cadastral para pacienteId={}", pacienteId);
            solicitacaoService.solicitarAtualizacaoCadastral(
                    pacienteId,
                    formCadastral.getPrimeiroNome(),
                    formCadastral.getSobrenome(),
                    formCadastral.getEmail(),
                    formCadastral.getTelefone(),
                    formCadastral.getMotivo()
            );
            formCadastral.setMotivo(null);
            capturarSnapshotCadastral();
            historicoCadastralModel.setRowCount(0);
            log.info("Solicitacao cadastral registrada para pacienteId={}", pacienteId);
            mensagem(FacesMessage.SEVERITY_INFO, "Solicitação registrada",
                    "Sua solicitação cadastral foi enviada e aguarda análise.");
        } catch (IllegalArgumentException e) {
            log.warn("Dados invalidos ao solicitar atualizacao cadastral: {}", e.getMessage());
            mensagem(FacesMessage.SEVERITY_WARN, "Dados inválidos", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao registrar solicitacao cadastral", e);
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro ao registrar", e.getMessage());
        }
    }

    public void solicitarAtualizacaoEndereco() {
        try {
            Long pacienteId = pacienteId();
            log.info("Solicitando atualizacao de endereco para pacienteId={}", pacienteId);
            solicitacaoService.solicitarAtualizacaoEndereco(
                    pacienteId,
                    formEndereco.getLogradouro(),
                    formEndereco.getNumero(),
                    formEndereco.getComplemento(),
                    formEndereco.getBairro(),
                    formEndereco.getCidade(),
                    formEndereco.getEstado(),
                    formEndereco.getCep(),
                    formEndereco.getMotivo()
            );
            formEndereco.setMotivo(null);
            capturarSnapshotEndereco();
            historicoCadastralModel.setRowCount(0);
            log.info("Solicitacao de endereco registrada para pacienteId={}", pacienteId);
            mensagem(FacesMessage.SEVERITY_INFO, "Solicitação registrada",
                    "Sua solicitação de endereço foi enviada e aguarda análise.");
        } catch (IllegalArgumentException e) {
            log.warn("Dados invalidos ao solicitar atualizacao de endereco: {}", e.getMessage());
            mensagem(FacesMessage.SEVERITY_WARN, "Dados inválidos", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao registrar solicitacao de endereco", e);
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro ao registrar", e.getMessage());
        }
    }

    public void solicitarAtualizacaoClinica() {
        try {
            Long pacienteId = pacienteId();
            log.info("Solicitando atualizacao clinica para pacienteId={}", pacienteId);
            solicitacaoService.solicitarAtualizacaoClinica(
                    pacienteId,
                    formClinico.getPeso(),
                    formClinico.getAltura(),
                    formClinico.getTipoSanguineo(),
                    formClinico.getMotivo()
            );
            formClinico  = new AtualizacaoClinicaDTO();
            capturarSnapshotClinico();
            historicoClinicoModel.setRowCount(0);
            log.info("Solicitacao clinica registrada para pacienteId={}", pacienteId);
            mensagem(FacesMessage.SEVERITY_INFO, "Solicitação registrada",
                    "Sua solicitação clínica foi enviada e aguarda análise.");
        } catch (IllegalArgumentException e) {
            log.warn("Dados invalidos ao solicitar atualizacao clinica: {}", e.getMessage());
            mensagem(FacesMessage.SEVERITY_WARN, "Dados inválidos", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao registrar solicitacao clinica", e);
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro ao registrar",
                    "Não foi possível registrar a solicitação. Tente novamente.");
        }
    }

    public void prepararCancelamento(Long id, Integer abaIndex) {
        this.idSolicitacaoParaCancelar = id;
        this.motivoCancelamento        = null;
        this.activeTabIndex            = abaIndex != null ? abaIndex : 0;
    }

    public void confirmarCancelamento() {
        try {
            Long pacienteId = pacienteId();
            Long solicitacaoId = idSolicitacaoParaCancelar;
            log.info("Confirmando cancelamento da solicitacao={} para pacienteId={}", idSolicitacaoParaCancelar, pacienteId);
            solicitacaoService.cancelarSolicitacao(
                    solicitacaoId,
                    pacienteId,
                    TipoCancelador.CLIENTE,
                    motivoCancelamento
            );
            idSolicitacaoParaCancelar = null;
            motivoCancelamento        = null;
            historicoCadastralModel.setRowCount(0);
            historicoClinicoModel.setRowCount(0);
            log.info("Solicitacao={} cancelada para pacienteId={}", solicitacaoId, pacienteId);
            mensagem(FacesMessage.SEVERITY_INFO, "Cancelada",
                    "Solicitação cancelada com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Nao foi possivel cancelar solicitacao={} : {}", idSolicitacaoParaCancelar, e.getMessage());
            mensagem(FacesMessage.SEVERITY_WARN, "Não foi possível cancelar", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao cancelar solicitacao={}", idSolicitacaoParaCancelar, e);
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Não foi possível cancelar. Tente novamente.");
        }
    }

    private void preencherFormCadastral(PacienteResponseDTO paciente) {
        if (paciente == null) return;
        String nomeCompleto = paciente.getNomeCompleto();
        if (nomeCompleto != null && !nomeCompleto.trim().isEmpty()) {
            int idx = nomeCompleto.indexOf(' ');
            formCadastral.setPrimeiroNome(idx > 0 ? nomeCompleto.substring(0, idx) : nomeCompleto);
            formCadastral.setSobrenome(idx > 0 ? nomeCompleto.substring(idx + 1) : "");
        }
        formCadastral.setEmail(paciente.getEmail());
        formCadastral.setTelefone(paciente.getTelefone());
    }

    private void preencherFormEndereco(Long pacienteId) {
        if (pacienteId == null) return;
        try {
            Endereco endereco = solicitacaoService.buscarEnderecoPorPaciente(pacienteId);
            if (endereco != null) {
                formEndereco.setLogradouro(endereco.getLogradouro());
                formEndereco.setNumero(endereco.getNumero());
                formEndereco.setComplemento(endereco.getComplemento());
                formEndereco.setBairro(endereco.getBairro());
                formEndereco.setCidade(endereco.getCidade());
                formEndereco.setEstado(endereco.getEstado());
                formEndereco.setCep(endereco.getCep());
            }
        } catch (Exception e) {
            log.warn("Nao foi possivel carregar endereco para pacienteId={}", pacienteId, e);
        }
    }

    private void preencherFormClinico(PacienteResponseDTO paciente) {
        if (paciente == null) return;
        if (paciente.getPeso() != null) {
            formClinico.setPeso(paciente.getPeso().doubleValue());
        }
        if (paciente.getAltura() != null) {
            formClinico.setAltura(paciente.getAltura().doubleValue());
        }
        if (paciente.getTipoSanguineo() != null) {
            formClinico.setTipoSanguineo(paciente.getTipoSanguineo().name());
        }
    }

    private void capturarSnapshotCadastral() {
        snpPrimeiroNome = formCadastral.getPrimeiroNome();
        snpSobrenome    = formCadastral.getSobrenome();
        snpEmail        = formCadastral.getEmail();
        snpTelefone     = formCadastral.getTelefone();
    }

    private void capturarSnapshotEndereco() {
        snpLogradouro  = formEndereco.getLogradouro();
        snpNumero      = formEndereco.getNumero();
        snpComplemento = formEndereco.getComplemento();
        snpBairro      = formEndereco.getBairro();
        snpCidade      = formEndereco.getCidade();
        snpEstado      = formEndereco.getEstado();
        snpCep         = formEndereco.getCep();
    }

    private void capturarSnapshotClinico() {
        snpPeso          = formClinico.getPeso();
        snpAltura        = formClinico.getAltura();
        snpTipoSanguineo = formClinico.getTipoSanguineo();
    }

    private Long pacienteId() {
        PacienteResponseDTO p = beanSessao.getPaciente();
        if (p == null) throw new IllegalArgumentException("Sessão inválida. Faça login novamente.");
        return p.getId();
    }

    private void mensagem(FacesMessage.Severity severidade, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severidade, resumo, detalhe));
    }

    public void selecionarSolicitacao(SolicitacaoAtualizacaoDTO solicitacao) {
        this.solicitacaoVisualizacao = solicitacao;
    }
}
