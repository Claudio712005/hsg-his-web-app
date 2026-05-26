package br.com.hsg.web.bean.admin;

import br.com.hsg.domain.entity.AgendaMedica;
import br.com.hsg.domain.entity.AgendaMedicaExcecao;
import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.MedicoEspecialidade;
import br.com.hsg.domain.enums.DiaSemana;
import br.com.hsg.domain.enums.TipoExcecaoAgenda;
import br.com.hsg.service.facade.admin.AgendaMedicaServiceFacade;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
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
import java.util.ArrayList;
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

    private ScheduleModel scheduleModelGeral = new DefaultScheduleModel();
    private ScheduleModel scheduleModelMulti = new DefaultScheduleModel();
    private List<Long> idsMedicosCalendario = new ArrayList<>();
    private int diasVisaoCalendario = 14;

    private int diasGerar = 30;
    private int diasVisaoSlots = 14;

    private List<Long> idsBulkSelecionados = new ArrayList<>();
    private int diasGerarBulk = 30;

    private String detalheTipo;
    private String detalheTitulo;
    private String detalheMedicoNome;
    private String detalheStatus;
    private String detalheInicio;
    private String detalheFim;
    private String detalheMotivo;
    private String detalheEspecialidade;
    private String detalheCorClasse;
    private String detalheIcone;

    private Long    formGradeId;
    private String  formGradeDiaSemana;
    private Date    formGradeHoraInicio;
    private Date    formGradeHoraFim;
    private Integer formGradeDuracaoMin = 30;
    private String  accordionFormIndex = "-1";

    private Date    formExcInicio;
    private Date    formExcFim;
    private String  formExcTipo;
    private String  formExcMotivo;

    @PostConstruct
    public void init() {
        this.medicos = agendaService.listarMedicosAtivos();
        if (!medicos.isEmpty()) {
            this.medicoSelecionadoId = medicos.get(0).getId();
            this.idsMedicosCalendario = new ArrayList<>();
            this.idsMedicosCalendario.add(this.medicoSelecionadoId);
            carregarDadosDoMedico();
        }
        novaGrade();
        novaExcecao();
        carregarCalendarioGeral();
        carregarCalendarioMulti();
    }

    public void aoMudarMedico() {
        carregarDadosDoMedico();
        if (medicoSelecionadoId != null && !idsMedicosCalendario.contains(medicoSelecionadoId)) {
            idsMedicosCalendario.add(medicoSelecionadoId);
            carregarCalendarioMulti();
        }
    }

    public void aoMudarMedicosCalendario() {
        carregarCalendarioMulti();
    }

    public void selecionarTodosCalendarioMulti() {
        this.idsMedicosCalendario = new ArrayList<>();
        if (medicos != null) {
            for (Medico m : medicos) {
                this.idsMedicosCalendario.add(m.getId());
            }
        }
        carregarCalendarioMulti();
    }

    public void limparCalendarioMulti() {
        this.idsMedicosCalendario = new ArrayList<>();
        carregarCalendarioMulti();
    }

    public void recarregarCalendarioGeral() {
        carregarCalendarioGeral();
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
            return;
        }
        LocalDateTime ini = LocalDate.now().atStartOfDay();
        LocalDateTime fim = ini.plusDays(diasVisaoSlots);
        this.slots = agendaService.listarSlotsPorMedicoPeriodo(medicoSelecionadoId, ini, fim);
    }

    private void carregarCalendarioGeral() {
        List<Long> ids = new ArrayList<>();
        if (medicos != null) {
            for (Medico m : medicos) ids.add(m.getId());
        }
        this.scheduleModelGeral = construirSchedule(ids);
    }

    private void carregarCalendarioMulti() {
        this.scheduleModelMulti = construirSchedule(idsMedicosCalendario);
    }

    private ScheduleModel construirSchedule(List<Long> idsMedicosBuscar) {
        ScheduleModel novo = new DefaultScheduleModel();
        if (idsMedicosBuscar == null || idsMedicosBuscar.isEmpty()) {
            return novo;
        }
        LocalDateTime ini = LocalDate.now().atStartOfDay();
        LocalDateTime fim = ini.plusDays(diasVisaoCalendario);
        List<AgendaMedicaSlot> slotsBusca =
                agendaService.listarSlotsPorMedicosPeriodo(idsMedicosBuscar, ini, fim);
        List<AgendaMedicaExcecao> excecoesBusca =
                agendaService.listarExcecoesPorMedicosVigentes(idsMedicosBuscar, ini, fim);

        for (AgendaMedicaSlot s : slotsBusca) {
            DefaultScheduleEvent ev = new DefaultScheduleEvent();
            String nomeMedico = abreviarNome(s.getMedico() != null ? s.getMedico().getNomeCompleto() : "");
            String medicoCompleto = s.getMedico() != null ? s.getMedico().getNomeCompleto() : "—";
            String esp = (s.getMedico() != null && s.getMedico().getEspecialidade() != null)
                    ? s.getMedico().getEspecialidade().getNome() : "";
            ev.setTitle(nomeMedico + " · " + s.getStatus().getDescricao()
                    + " · " + formatarHora(s.getDataInicio().toLocalTime()));
            ev.setStartDate(toDate(s.getDataInicio()));
            ev.setEndDate(toDate(s.getDataFim()));
            ev.setStyleClass(cssClasseStatus(s) + " " + cssClasseMedico(s.getMedico() != null ? s.getMedico().getId() : null));
            ev.setEditable(false);
            ev.setDescription("SLOT|" + medicoCompleto + "|" + s.getStatus().getDescricao()
                    + "|" + formatarData(s.getDataInicio()) + "|" + formatarData(s.getDataFim())
                    + "|" + esp + "|" + cssClasseStatus(s));
            novo.addEvent(ev);
        }
        for (AgendaMedicaExcecao e : excecoesBusca) {
            DefaultScheduleEvent ev = new DefaultScheduleEvent();
            String nomeMedico = abreviarNome(e.getMedico() != null ? e.getMedico().getNomeCompleto() : "");
            String medicoCompleto = e.getMedico() != null ? e.getMedico().getNomeCompleto() : "—";
            ev.setTitle(nomeMedico + " · " + e.getTipo().getDescricao()
                    + (e.getMotivo() != null ? " — " + e.getMotivo() : ""));
            ev.setStartDate(toDate(e.getDataInicio()));
            ev.setEndDate(toDate(e.getDataFim()));
            ev.setStyleClass("evt-excecao " + cssClasseMedico(e.getMedico() != null ? e.getMedico().getId() : null));
            ev.setEditable(false);
            ev.setDescription("EXCECAO|" + medicoCompleto + "|" + e.getTipo().getDescricao()
                    + "|" + formatarData(e.getDataInicio()) + "|" + formatarData(e.getDataFim())
                    + "|" + (e.getMotivo() != null ? e.getMotivo() : "") + "|evt-excecao");
            novo.addEvent(ev);
        }
        return novo;
    }

    public void onEventSelect(SelectEvent event) {
        Object obj = event.getObject();
        if (!(obj instanceof ScheduleEvent)) return;
        ScheduleEvent ev = (ScheduleEvent) obj;
        String desc = ev.getDescription();
        if (desc == null || desc.isEmpty()) return;
        String[] partes = desc.split("\\|", -1);
        if (partes.length < 7) return;

        this.detalheTipo          = partes[0];
        this.detalheMedicoNome    = partes[1];
        this.detalheStatus        = partes[2];
        this.detalheInicio        = partes[3];
        this.detalheFim           = partes[4];
        this.detalheMotivo        = "EXCECAO".equals(partes[0]) ? partes[5] : null;
        this.detalheEspecialidade = "SLOT".equals(partes[0])    ? partes[5] : null;
        this.detalheCorClasse     = partes[6];

        if ("EXCECAO".equals(partes[0])) {
            this.detalheTitulo = "Exceção da agenda";
            this.detalheIcone  = "fa-calendar-xmark";
        } else {
            this.detalheTitulo = "Horário (slot)";
            this.detalheIcone  = "fa-clock";
        }
    }

    private String cssClasseMedico(Long idMedico) {
        if (idMedico == null || medicos == null) return "";
        int idx = -1;
        for (int i = 0; i < medicos.size(); i++) {
            if (idMedico.equals(medicos.get(i).getId())) { idx = i; break; }
        }
        if (idx < 0) return "";
        return "med-c" + ((idx % 8) + 1);
    }

    private String abreviarNome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isEmpty()) return "";
        String[] partes = nomeCompleto.trim().split("\\s+");
        if (partes.length == 1) return partes[0];
        return partes[0] + " " + partes[partes.length - 1].substring(0, 1) + ".";
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
        this.accordionFormIndex   = "-1";
    }

    public void prepararEdicaoGrade(AgendaMedica g) {
        this.formGradeId         = g.getId();
        this.formGradeDiaSemana  = g.getDiaSemana() != null ? g.getDiaSemana().name() : null;
        this.formGradeHoraInicio = toDate(g.getHoraInicio());
        this.formGradeHoraFim    = toDate(g.getHoraFim());
        this.formGradeDuracaoMin = g.getDuracaoMinutos();
        this.accordionFormIndex  = "0";
    }

    public void abrirAccordionForm() {
        this.accordionFormIndex = "0";
    }

    public void salvarGrade() {
        try {
            if (medicoSelecionadoId == null) {
                msg(FacesMessage.SEVERITY_WARN, "Selecione um médico antes de cadastrar uma faixa.");
                return;
            }
            if (formGradeHoraInicio == null || formGradeHoraFim == null) {
                msg(FacesMessage.SEVERITY_WARN, "Informe os horários de início e fim.");
                return;
            }
            if (formGradeDuracaoMin == null || formGradeDuracaoMin <= 0) {
                msg(FacesMessage.SEVERITY_WARN, "Informe uma duração de slot válida.");
                return;
            }
            if (formGradeId == null && (formGradeDiaSemana == null || formGradeDiaSemana.isEmpty())) {
                msg(FacesMessage.SEVERITY_WARN, "Selecione o dia da semana.");
                return;
            }
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
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[AgendaMedicaBean] Falha ao salvar grade", ex);
            msg(FacesMessage.SEVERITY_ERROR,
                extrairMensagem(ex, "Erro inesperado ao salvar grade."));
        }
    }

    public void inativarGrade(Long id) {
        try {
            agendaService.inativarGrade(id);
            msg(FacesMessage.SEVERITY_INFO, "Grade inativada.");
            carregarDadosDoMedico();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[AgendaMedicaBean] Falha ao inativar grade", ex);
            msg(FacesMessage.SEVERITY_ERROR,
                extrairMensagem(ex, "Erro ao inativar grade."));
        }
    }

    public void ativarGrade(Long id) {
        try {
            agendaService.ativarGrade(id);
            msg(FacesMessage.SEVERITY_INFO, "Grade ativada.");
            carregarDadosDoMedico();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[AgendaMedicaBean] Falha ao ativar grade", ex);
            msg(FacesMessage.SEVERITY_ERROR,
                extrairMensagem(ex, "Erro ao ativar grade."));
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
            if (medicoSelecionadoId == null) {
                msg(FacesMessage.SEVERITY_WARN, "Selecione um médico antes de cadastrar uma exceção.");
                return;
            }
            if (formExcInicio == null || formExcFim == null) {
                msg(FacesMessage.SEVERITY_WARN, "Informe as datas de início e fim.");
                return;
            }
            LocalDateTime ini = toLocalDateTime(formExcInicio);
            LocalDateTime fim = toLocalDateTime(formExcFim);
            if (!fim.isAfter(ini)) {
                msg(FacesMessage.SEVERITY_WARN, "A data de fim deve ser posterior à data de início.");
                return;
            }
            TipoExcecaoAgenda tipo = formExcTipo != null
                    ? TipoExcecaoAgenda.fromValor(formExcTipo) : TipoExcecaoAgenda.BLOQUEIO;
            agendaService.criarExcecao(medicoSelecionadoId, ini, fim, tipo, formExcMotivo);
            msg(FacesMessage.SEVERITY_INFO, "Exceção cadastrada.");
            carregarDadosDoMedico();
            novaExcecao();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[AgendaMedicaBean] Falha ao salvar exceção", ex);
            msg(FacesMessage.SEVERITY_ERROR,
                extrairMensagem(ex, "Erro inesperado ao salvar exceção."));
        }
    }

    public void removerExcecao(Long id) {
        try {
            agendaService.removerExcecao(id);
            msg(FacesMessage.SEVERITY_INFO, "Exceção removida.");
            carregarDadosDoMedico();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[AgendaMedicaBean] Falha ao remover exceção", ex);
            msg(FacesMessage.SEVERITY_ERROR,
                extrairMensagem(ex, "Erro ao remover exceção."));
        }
    }

    public void gerarSlots() {
        try {
            if (medicoSelecionadoId == null) {
                msg(FacesMessage.SEVERITY_WARN, "Selecione um médico para gerar slots.");
                return;
            }
            if (diasGerar < 1 || diasGerar > 180) {
                msg(FacesMessage.SEVERITY_WARN, "Informe um período entre 1 e 180 dias.");
                return;
            }
            int n = agendaService.gerarSlots(medicoSelecionadoId, diasGerar);
            msg(FacesMessage.SEVERITY_INFO, n + " slot(s) gerado(s) para os próximos " + diasGerar + " dias.");
            carregarDadosDoMedico();
            carregarCalendarioGeral();
            carregarCalendarioMulti();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[AgendaMedicaBean] Falha ao gerar slots", ex);
            msg(FacesMessage.SEVERITY_ERROR,
                extrairMensagem(ex, "Erro inesperado ao gerar slots."));
        }
    }

    public void prepararGeracaoEmMassa() {
        this.idsBulkSelecionados = new ArrayList<>();
        this.diasGerarBulk = this.diasGerar;
    }

    public void selecionarTodosMedicosBulk() {
        this.idsBulkSelecionados = new ArrayList<>();
        if (medicos != null) {
            for (Medico m : medicos) {
                this.idsBulkSelecionados.add(m.getId());
            }
        }
    }

    public void limparSelecaoBulk() {
        this.idsBulkSelecionados = new ArrayList<>();
    }

    public void gerarSlotsEmMassa() {
        if (idsBulkSelecionados == null || idsBulkSelecionados.isEmpty()) {
            msg(FacesMessage.SEVERITY_WARN, "Selecione ao menos um médico.");
            return;
        }
        if (diasGerarBulk < 1 || diasGerarBulk > 180) {
            msg(FacesMessage.SEVERITY_WARN, "Informe um período entre 1 e 180 dias.");
            return;
        }
        int totalSlots = 0;
        int totalMedicos = 0;
        int falhas = 0;
        for (Long id : idsBulkSelecionados) {
            try {
                totalSlots += agendaService.gerarSlots(id, diasGerarBulk);
                totalMedicos++;
            } catch (Exception ex) {
                falhas++;
                LOG.log(Level.WARNING, "[AgendaMedicaBean] Falha ao gerar slots em massa para medico " + id, ex);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(totalSlots).append(" slot(s) gerado(s) para ")
          .append(totalMedicos).append(" médico(s) — próximos ")
          .append(diasGerarBulk).append(" dias.");
        if (falhas > 0) {
            sb.append(" ").append(falhas).append(" médico(s) falharam.");
            msg(FacesMessage.SEVERITY_WARN, sb.toString());
        } else {
            msg(FacesMessage.SEVERITY_INFO, sb.toString());
        }
        carregarDadosDoMedico();
        carregarCalendarioGeral();
        carregarCalendarioMulti();
    }

    public String labelMedicoDropdown(Medico m) {
        if (m == null) return "";
        StringBuilder sb = new StringBuilder("Dr(a). ").append(m.getNomeCompleto());
        if (m.getCrm() != null) {
            sb.append(" — ").append(m.getCrm().getFormatado());
        }
        if (m.getEspecialidade() != null && m.getEspecialidade().getNome() != null) {
            sb.append(" • ").append(m.getEspecialidade().getNome());
        }
        return sb.toString();
    }

    public int getTotalMedicosAtivos() {
        return medicos == null ? 0 : medicos.size();
    }

    public Medico getMedicoSelecionado() {
        if (medicoSelecionadoId == null || medicos == null) return null;
        for (Medico m : medicos) {
            if (medicoSelecionadoId.equals(m.getId())) return m;
        }
        return null;
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

    /**
     * Extrai a mensagem útil do throwable. EJB envolve RuntimeException em EJBException;
     * percorre a cadeia de causes até achar IllegalArgument/IllegalState com mensagem.
     */
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
        if (t != null && t.getMessage() != null && !t.getMessage().isEmpty()) {
            return t.getMessage();
        }
        return fallback;
    }

    public List<Medico> getMedicos()                          { return medicos; }
    public Long getMedicoSelecionadoId()                      { return medicoSelecionadoId; }
    public void setMedicoSelecionadoId(Long v)                { this.medicoSelecionadoId = v; }

    public List<AgendaMedica> getGrades()                     { return grades; }
    public List<AgendaMedicaExcecao> getExcecoes()            { return excecoes; }
    public List<AgendaMedicaSlot> getSlots()                  { return slots; }
    public List<MedicoEspecialidade> getEspecialidadesMedico(){ return especialidadesMedico; }
    public long getSlotsLivresFuturos()                       { return slotsLivresFuturos; }
    public ScheduleModel getScheduleModelGeral()              { return scheduleModelGeral; }
    public ScheduleModel getScheduleModelMulti()              { return scheduleModelMulti; }
    public List<Long> getIdsMedicosCalendario()               { return idsMedicosCalendario; }
    public void setIdsMedicosCalendario(List<Long> v)         { this.idsMedicosCalendario = v; }
    public int getDiasVisaoCalendario()                       { return diasVisaoCalendario; }
    public void setDiasVisaoCalendario(int v)                 { this.diasVisaoCalendario = v; carregarCalendarioGeral(); carregarCalendarioMulti(); }

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
    public String getAccordionFormIndex()                 { return accordionFormIndex; }
    public void setAccordionFormIndex(String v)           { this.accordionFormIndex = v; }

    public String getDetalheTipo()           { return detalheTipo; }
    public String getDetalheTitulo()         { return detalheTitulo; }
    public String getDetalheMedicoNome()     { return detalheMedicoNome; }
    public String getDetalheStatus()         { return detalheStatus; }
    public String getDetalheInicio()         { return detalheInicio; }
    public String getDetalheFim()            { return detalheFim; }
    public String getDetalheMotivo()         { return detalheMotivo; }
    public String getDetalheEspecialidade()  { return detalheEspecialidade; }
    public String getDetalheCorClasse()      { return detalheCorClasse; }
    public String getDetalheIcone()          { return detalheIcone; }

    public List<Long> getIdsBulkSelecionados()           { return idsBulkSelecionados; }
    public void setIdsBulkSelecionados(List<Long> v)     { this.idsBulkSelecionados = v; }
    public int getDiasGerarBulk()                        { return diasGerarBulk; }
    public void setDiasGerarBulk(int v)                  { this.diasGerarBulk = v; }

    public Date getFormExcInicio()       { return formExcInicio; }
    public void setFormExcInicio(Date v) { this.formExcInicio = v; }
    public Date getFormExcFim()          { return formExcFim; }
    public void setFormExcFim(Date v)    { this.formExcFim = v; }
    public String getFormExcTipo()                { return formExcTipo; }
    public void setFormExcTipo(String v)          { this.formExcTipo = v; }
    public String getFormExcMotivo()              { return formExcMotivo; }
    public void setFormExcMotivo(String v)        { this.formExcMotivo = v; }
}
