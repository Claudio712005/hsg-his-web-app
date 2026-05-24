package br.com.hsg.web.bean.admin;

import br.com.hsg.domain.entity.AgendaMedica;
import br.com.hsg.domain.entity.AgendaMedicaExcecao;
import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.MedicoEspecialidade;
import br.com.hsg.domain.enums.DiaSemana;
import br.com.hsg.domain.enums.TipoExcecaoAgenda;
import br.com.hsg.service.facade.admin.AgendaMedicaServiceFacade;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@Named("agendaMedicaBean")
public class AgendaMedicaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AgendaMedicaBean.class.getName());
    private static final DateTimeFormatter FMT_DT     = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_HORA   = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_DATA   = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @EJB private AgendaMedicaServiceFacade agendaService;

    private List<Medico> medicos;
    private Long medicoSelecionadoId;

    private List<AgendaMedica> grades = Collections.emptyList();
    private List<AgendaMedicaExcecao> excecoes = Collections.emptyList();
    private List<AgendaMedicaSlot> slots = Collections.emptyList();
    private List<MedicoEspecialidade> especialidadesMedico = Collections.emptyList();
    private long slotsLivresFuturos;
    private ScheduleModel scheduleModel = new DefaultScheduleModel();

    private int diasGerar = 30;
    private int diasVisaoSlots = 14;

    private Long    formGradeId;
    private String  formGradeDiaSemana;
    private Date    formGradeHoraInicio;
    private Date    formGradeHoraFim;
    private Integer formGradeDuracaoMin = 30;

    private Date    formExcInicio;
    private Date    formExcFim;
    private String  formExcTipo;
    private String  formExcMotivo;

    @PostConstruct
    public void init() {
        this.medicos = agendaService.listarMedicosAtivos();
        if (!medicos.isEmpty()) {
            this.medicoSelecionadoId = medicos.get(0).getId();
            carregarDadosDoMedico();
        }
        novaGrade();
        novaExcecao();
    }

    public void aoMudarMedico() {
        carregarDadosDoMedico();
    }

    private void carregarDadosDoMedico() {
        if (medicoSelecionadoId == null) {
            grades = Collections.emptyList();
            excecoes = Collections.emptyList();
            slots = Collections.emptyList();
            especialidadesMedico = Collections.emptyList();
            slotsLivresFuturos = 0;
            return;
        }
        this.grades               = agendaService.listarGradesPorMedico(medicoSelecionadoId);
        this.excecoes             = agendaService.listarExcecoesPorMedico(medicoSelecionadoId);
        this.especialidadesMedico = agendaService.listarEspecialidadesDoMedico(medicoSelecionadoId);
        this.slotsLivresFuturos   = agendaService.contarSlotsLivresFuturos(medicoSelecionadoId);
        carregarSlots();
    }

    private void carregarSlots() {
        if (medicoSelecionadoId == null) {
            slots = Collections.emptyList();
            scheduleModel = new DefaultScheduleModel();
            return;
        }
        LocalDateTime ini = LocalDate.now().atStartOfDay();
        LocalDateTime fim = ini.plusDays(diasVisaoSlots);
        this.slots = agendaService.listarSlotsPorMedicoPeriodo(medicoSelecionadoId, ini, fim);
        montarSchedule();
    }

    private void montarSchedule() {
        ScheduleModel novo = new DefaultScheduleModel();
        for (AgendaMedicaSlot s : slots) {
            DefaultScheduleEvent ev = new DefaultScheduleEvent();
            ev.setTitle(s.getStatus().getDescricao() + " · " + formatarHora(s.getDataInicio().toLocalTime()));
            ev.setStartDate(toDate(s.getDataInicio()));
            ev.setEndDate(toDate(s.getDataFim()));
            ev.setStyleClass(cssClasseStatus(s));
            ev.setEditable(false);
            novo.addEvent(ev);
        }
        for (AgendaMedicaExcecao e : excecoes) {
            if (e.getDataFim().isBefore(LocalDateTime.now())) continue;
            DefaultScheduleEvent ev = new DefaultScheduleEvent();
            ev.setTitle(e.getTipo().getDescricao()
                    + (e.getMotivo() != null ? " — " + e.getMotivo() : ""));
            ev.setStartDate(toDate(e.getDataInicio()));
            ev.setEndDate(toDate(e.getDataFim()));
            ev.setStyleClass("evt-excecao");
            ev.setEditable(false);
            novo.addEvent(ev);
        }
        this.scheduleModel = novo;
    }

    private String cssClasseStatus(AgendaMedicaSlot s) {
        switch (s.getStatus()) {
            case LIVRE:      return "evt-livre";
            case RESERVADO:  return "evt-reservado";
            case BLOQUEADO:  return "evt-bloqueado";
            case CANCELADO:  return "evt-cancelado";
            default:         return "";
        }
    }


    public void novaGrade() {
        this.formGradeId          = null;
        this.formGradeDiaSemana   = null;
        this.formGradeHoraInicio  = toDate(LocalTime.of(8, 0));
        this.formGradeHoraFim     = toDate(LocalTime.of(12, 0));
        this.formGradeDuracaoMin  = 30;
    }

    public void prepararEdicaoGrade(AgendaMedica g) {
        this.formGradeId         = g.getId();
        this.formGradeDiaSemana  = g.getDiaSemana() != null ? g.getDiaSemana().name() : null;
        this.formGradeHoraInicio = toDate(g.getHoraInicio());
        this.formGradeHoraFim    = toDate(g.getHoraFim());
        this.formGradeDuracaoMin = g.getDuracaoMinutos();
    }

    public void salvarGrade() {
        try {
            LocalTime hi = toLocalTime(formGradeHoraInicio);
            LocalTime hf = toLocalTime(formGradeHoraFim);
            if (formGradeId == null) {
                DiaSemana dow = DiaSemana.valueOf(formGradeDiaSemana);
                agendaService.criarGrade(medicoSelecionadoId, dow, hi, hf, formGradeDuracaoMin);
                msg(FacesMessage.SEVERITY_INFO, "Grade cadastrada.");
            } else {
                agendaService.atualizarGrade(formGradeId, hi, hf, formGradeDuracaoMin);
                msg(FacesMessage.SEVERITY_INFO, "Grade atualizada.");
            }
            carregarDadosDoMedico();
            novaGrade();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            msg(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[AgendaMedicaBean] Erro ao salvar grade", ex);
            msg(FacesMessage.SEVERITY_ERROR, "Erro inesperado ao salvar grade.");
        }
    }

    public void inativarGrade(Long id) {
        try {
            agendaService.inativarGrade(id);
            msg(FacesMessage.SEVERITY_INFO, "Grade inativada.");
            carregarDadosDoMedico();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            msg(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public void ativarGrade(Long id) {
        try {
            agendaService.ativarGrade(id);
            msg(FacesMessage.SEVERITY_INFO, "Grade ativada.");
            carregarDadosDoMedico();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            msg(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public void novaExcecao() {
        this.formExcInicio = null;
        this.formExcFim    = null;
        this.formExcTipo   = TipoExcecaoAgenda.BLOQUEIO.getValor();
        this.formExcMotivo = null;
    }

    public void salvarExcecao() {
        try {
            TipoExcecaoAgenda tipo = formExcTipo != null
                    ? TipoExcecaoAgenda.fromValor(formExcTipo) : TipoExcecaoAgenda.BLOQUEIO;
            LocalDateTime ini = toLocalDateTime(formExcInicio);
            LocalDateTime fim = toLocalDateTime(formExcFim);
            agendaService.criarExcecao(medicoSelecionadoId, ini, fim, tipo, formExcMotivo);
            msg(FacesMessage.SEVERITY_INFO, "Exceção cadastrada.");
            carregarDadosDoMedico();
            novaExcecao();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            msg(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[AgendaMedicaBean] Erro ao salvar exceção", ex);
            msg(FacesMessage.SEVERITY_ERROR, "Erro inesperado ao salvar exceção.");
        }
    }

    public void removerExcecao(Long id) {
        try {
            agendaService.removerExcecao(id);
            msg(FacesMessage.SEVERITY_INFO, "Exceção removida.");
            carregarDadosDoMedico();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[AgendaMedicaBean] Erro ao remover exceção", ex);
            msg(FacesMessage.SEVERITY_ERROR, "Erro inesperado.");
        }
    }

    public void gerarSlots() {
        try {
            int n = agendaService.gerarSlots(medicoSelecionadoId, diasGerar);
            msg(FacesMessage.SEVERITY_INFO, n + " slot(s) gerado(s) para os próximos " + diasGerar + " dias.");
            carregarDadosDoMedico();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            msg(FacesMessage.SEVERITY_WARN, ex.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[AgendaMedicaBean] Erro ao gerar slots", ex);
            msg(FacesMessage.SEVERITY_ERROR, "Erro inesperado ao gerar slots.");
        }
    }

    public DiaSemana[] getDiasSemana()            { return DiaSemana.values(); }
    public TipoExcecaoAgenda[] getTiposExcecao()  { return TipoExcecaoAgenda.values(); }

    public boolean temFaixaNoDia(DiaSemana d) {
        if (grades == null || d == null) return false;
        for (AgendaMedica g : grades) {
            if (d.equals(g.getDiaSemana())) return true;
        }
        return false;
    }

    public String formatarData(LocalDateTime dt)  { return dt != null ? dt.format(FMT_DT) : "—"; }
    public String formatarDataSimples(LocalDate d){ return d != null ? d.format(FMT_DATA) : "—"; }
    public String formatarHora(LocalTime t)       { return t != null ? t.format(FMT_HORA) : "—"; }

    private Date toDate(LocalTime t) {
        if (t == null) return null;
        return Date.from(LocalDateTime.of(LocalDate.now(), t).atZone(ZoneId.systemDefault()).toInstant());
    }
    private Date toDate(LocalDateTime dt) {
        if (dt == null) return null;
        return Date.from(dt.atZone(ZoneId.systemDefault()).toInstant());
    }
    private LocalTime toLocalTime(Date d) {
        if (d == null) return null;
        return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()).toLocalTime();
    }
    private LocalDateTime toLocalDateTime(Date d) {
        if (d == null) return null;
        return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }

    private void msg(FacesMessage.Severity severity, String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, mensagem, null));
    }

    public List<Medico> getMedicos()                          { return medicos; }
    public Long getMedicoSelecionadoId()                      { return medicoSelecionadoId; }
    public void setMedicoSelecionadoId(Long v)                { this.medicoSelecionadoId = v; }

    public List<AgendaMedica> getGrades()                     { return grades; }
    public List<AgendaMedicaExcecao> getExcecoes()            { return excecoes; }
    public List<AgendaMedicaSlot> getSlots()                  { return slots; }
    public List<MedicoEspecialidade> getEspecialidadesMedico(){ return especialidadesMedico; }
    public long getSlotsLivresFuturos()                       { return slotsLivresFuturos; }
    public ScheduleModel getScheduleModel()                   { return scheduleModel; }

    public int getDiasGerar()             { return diasGerar; }
    public void setDiasGerar(int v)       { this.diasGerar = v; }
    public int getDiasVisaoSlots()        { return diasVisaoSlots; }
    public void setDiasVisaoSlots(int v)  { this.diasVisaoSlots = v; }

    public Long getFormGradeId()                          { return formGradeId; }
    public void setFormGradeId(Long v)                    { this.formGradeId = v; }
    public String getFormGradeDiaSemana()                 { return formGradeDiaSemana; }
    public void setFormGradeDiaSemana(String v)           { this.formGradeDiaSemana = v; }
    public Date getFormGradeHoraInicio()                  { return formGradeHoraInicio; }
    public void setFormGradeHoraInicio(Date v)            { this.formGradeHoraInicio = v; }
    public Date getFormGradeHoraFim()                     { return formGradeHoraFim; }
    public void setFormGradeHoraFim(Date v)               { this.formGradeHoraFim = v; }
    public Integer getFormGradeDuracaoMin()               { return formGradeDuracaoMin; }
    public void setFormGradeDuracaoMin(Integer v)         { this.formGradeDuracaoMin = v; }

    public Date getFormExcInicio()       { return formExcInicio; }
    public void setFormExcInicio(Date v) { this.formExcInicio = v; }
    public Date getFormExcFim()          { return formExcFim; }
    public void setFormExcFim(Date v)    { this.formExcFim = v; }
    public String getFormExcTipo()                { return formExcTipo; }
    public void setFormExcTipo(String v)          { this.formExcTipo = v; }
    public String getFormExcMotivo()              { return formExcMotivo; }
    public void setFormExcMotivo(String v)        { this.formExcMotivo = v; }
}
