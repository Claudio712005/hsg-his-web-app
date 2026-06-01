package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.service.dto.ResultadoFinanceiroConsulta;
import br.com.hsg.service.facade.paciente.ConsultaBuscaServiceFacade;
import br.com.hsg.service.facade.paciente.ConsultaServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("consultaBuscaBean")
public class ConsultaBuscaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ConsultaBuscaBean.class.getName());
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_HR = DateTimeFormatter.ofPattern("HH:mm");

    @Inject private BeanSessao beanSessao;
    @EJB    private ConsultaBuscaServiceFacade buscaService;
    @EJB    private ConsultaServiceFacade consultaService;

    private static final int DIAS_FRENTE = 30;
    private static final DateTimeFormatter FMT_DIA = DateTimeFormatter.ofPattern("EEE, dd/MM", new Locale("pt", "BR"));

    private List<Especialidade> especialidades = Collections.emptyList();
    private List<Medico> medicos = Collections.emptyList();
    private List<DiaHorarios> agenda = Collections.emptyList();

    private Long especialidadeSelecionadaId;
    private Long medicoSelecionadoId;
    private boolean buscou;

    private PacienteConvenio convenioAtivo;

    private AgendaMedicaSlot slotSelecionado;
    private boolean usarConvenio;
    private ResultadoFinanceiroConsulta resultado;

    @PostConstruct
    public void init() {
        this.especialidades = buscaService.listarEspecialidadesAtivas();
        if (getPacienteId() != null) {
            this.convenioAtivo = buscaService.buscarConvenioAtivo(getPacienteId());
        }
    }

    public void aoMudarEspecialidade() {
        this.medicoSelecionadoId = null;
        this.medicos = especialidadeSelecionadaId != null
                ? buscaService.listarMedicosPorEspecialidade(especialidadeSelecionadaId)
                : Collections.emptyList();
        this.agenda = Collections.emptyList();
        this.buscou = false;
        if (especialidadeSelecionadaId != null) {
            buscarHorarios();
        }
    }

    public void aoMudarMedico() {
        if (especialidadeSelecionadaId != null) {
            buscarHorarios();
        }
    }

    public void buscarHorarios() {
        try {
            if (especialidadeSelecionadaId == null) {
                msg(FacesMessage.SEVERITY_WARN, "Selecione uma especialidade.");
                return;
            }
            List<AgendaMedicaSlot> slots = buscaService.listarHorariosLivresProximos(
                    especialidadeSelecionadaId, DIAS_FRENTE, medicoSelecionadoId);
            this.agenda = agruparPorDia(slots);
            this.buscou = true;
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[ConsultaBuscaBean] Falha ao buscar horários", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao buscar horários."));
        }
    }

    private List<DiaHorarios> agruparPorDia(List<AgendaMedicaSlot> slots) {
        java.util.LinkedHashMap<LocalDate, DiaHorarios> mapa = new java.util.LinkedHashMap<>();
        for (AgendaMedicaSlot s : slots) {
            LocalDate dia = s.getDataInicio().toLocalDate();
            DiaHorarios d = mapa.get(dia);
            if (d == null) {
                d = new DiaHorarios(dia);
                mapa.put(dia, d);
            }
            d.getSlots().add(s);
        }
        return new java.util.ArrayList<>(mapa.values());
    }

    public int getDiasFrente() { return DIAS_FRENTE; }

    public void prepararAgendamento(AgendaMedicaSlot slot) {
        this.slotSelecionado = slot;
        this.usarConvenio = convenioAtivo != null;
        recalcularFinanceiro();
    }

    public void recalcularFinanceiro() {
        if (slotSelecionado == null || getPacienteId() == null) {
            this.resultado = null;
            return;
        }
        try {
            this.resultado = consultaService.simular(getPacienteId(), slotSelecionado.getId(), usarConvenio);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[ConsultaBuscaBean] Falha ao simular financeiro", ex);
            this.resultado = null;
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao calcular valores."));
        }
    }

    public void confirmarAgendamento() {
        try {
            if (slotSelecionado == null) {
                msg(FacesMessage.SEVERITY_WARN, "Selecione um horário.");
                return;
            }
            if (getPacienteId() == null) {
                msg(FacesMessage.SEVERITY_ERROR, "Sessão de paciente não encontrada.");
                return;
            }
            Consulta c = consultaService.agendar(getPacienteId(), especialidadeSelecionadaId,
                    slotSelecionado.getId(), usarConvenio);
            msg(FacesMessage.SEVERITY_INFO, "Consulta agendada para "
                    + formatarDataHora(c.getDataConsulta()) + ".");
            this.slotSelecionado = null;
            this.resultado = null;
            buscarHorarios();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[ConsultaBuscaBean] Falha ao agendar", ex);
            msg(FacesMessage.SEVERITY_ERROR, extrairMensagem(ex, "Erro ao agendar consulta."));
        }
    }

    public String nomeEspecialidadeSelecionada() {
        if (especialidadeSelecionadaId == null) return null;
        for (Especialidade e : especialidades) {
            if (especialidadeSelecionadaId.equals(e.getId())) return e.getNome();
        }
        return null;
    }

    public String formatarDataHora(LocalDateTime dt) { return dt != null ? dt.format(FMT_DT) : "—"; }
    public String formatarHora(LocalDateTime dt)     { return dt != null ? dt.format(FMT_HR) : "—"; }

    private Long getPacienteId() {
        return beanSessao.getPaciente() != null ? beanSessao.getPaciente().getId() : null;
    }

    private void msg(FacesMessage.Severity sev, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, texto, null));
    }

    private String extrairMensagem(Throwable t, String fallback) {
        Throwable cur = t;
        int guard = 0;
        while (cur != null && guard < 10) {
            if ((cur instanceof IllegalArgumentException || cur instanceof IllegalStateException)
                    && cur.getMessage() != null && !cur.getMessage().isEmpty()) {
                return cur.getMessage();
            }
            cur = cur.getCause();
            guard++;
        }
        return (t != null && t.getMessage() != null && !t.getMessage().isEmpty()) ? t.getMessage() : fallback;
    }

    public List<Especialidade> getEspecialidades()      { return especialidades; }
    public List<Medico> getMedicos()                    { return medicos; }
    public List<DiaHorarios> getAgenda()                { return agenda; }
    public Long getEspecialidadeSelecionadaId()         { return especialidadeSelecionadaId; }
    public void setEspecialidadeSelecionadaId(Long v)   { this.especialidadeSelecionadaId = v; }
    public Long getMedicoSelecionadoId()                { return medicoSelecionadoId; }
    public void setMedicoSelecionadoId(Long v)          { this.medicoSelecionadoId = v; }
    public boolean isBuscou()                           { return buscou; }
    public PacienteConvenio getConvenioAtivo()          { return convenioAtivo; }
    public AgendaMedicaSlot getSlotSelecionado()        { return slotSelecionado; }
    public boolean isUsarConvenio()                     { return usarConvenio; }
    public void setUsarConvenio(boolean v)              { this.usarConvenio = v; }
    public ResultadoFinanceiroConsulta getResultado()   { return resultado; }

    public static class DiaHorarios implements Serializable {
        private static final long serialVersionUID = 1L;
        private final LocalDate data;
        private final List<AgendaMedicaSlot> slots = new java.util.ArrayList<>();

        public DiaHorarios(LocalDate data) { this.data = data; }
        public LocalDate getData()         { return data; }
        public List<AgendaMedicaSlot> getSlots() { return slots; }
        public String getLabel() {
            String txt = data.format(FMT_DIA);
            return Character.toUpperCase(txt.charAt(0)) + txt.substring(1);
        }
    }
}
