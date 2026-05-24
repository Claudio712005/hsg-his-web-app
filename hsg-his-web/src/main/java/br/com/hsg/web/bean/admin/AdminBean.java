package br.com.hsg.web.bean.admin;

import br.com.hsg.domain.entity.Enfermeiro;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.service.facade.admin.AdminServiceFacade;
import br.com.hsg.service.facade.clinica.ClinicaServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.model.EnfermeiroLazyModel;
import br.com.hsg.web.model.EspecialidadeLazyModel;
import br.com.hsg.web.model.MedicoLazyModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ViewScoped
@Named("adminBean")
public class AdminBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT_DT  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject
    private BeanSessao beanSessao;

    @EJB
    private ClinicaServiceFacade clinicaService;

    @EJB
    private AdminServiceFacade adminService;

    private MedicoLazyModel        medicoModel;
    private EnfermeiroLazyModel    enfermeiroModel;
    private EspecialidadeLazyModel especialidadeModel;

    private String filtroNomeMedico;
    private String filtroStatusMedico;

    private String filtroNomeEnfermeiro;
    private String filtroSetorEnfermeiro;
    private String filtroStatusEnfermeiro;

    private String filtroNomeEspecialidade;
    private String filtroAreaEspecialidade;
    private String filtroStatusEspecialidade;

    private long totalMedicosAtivos;
    private long totalEnfermeirosAtivos;
    private long totalEspecialidades;
    private long totalAdmins;

    private Medico        medicoSelecionado;
    private Enfermeiro    enfermeiroSelecionado;
    private Especialidade especialidadeSelecionada;

    @PostConstruct
    public void init() {
        medicoModel         = new MedicoLazyModel(clinicaService);
        enfermeiroModel     = new EnfermeiroLazyModel(clinicaService);
        especialidadeModel  = new EspecialidadeLazyModel(clinicaService);
        totalMedicosAtivos   = clinicaService.contarMedicos(null, "A");
        totalEnfermeirosAtivos = clinicaService.contarEnfermeiros(null, null, "A");
        totalEspecialidades  = clinicaService.contarEspecialidadesAtivas();
        totalAdmins          = adminService.contarAdmins();
    }

    public void selecionarMedico(Medico m) {
        this.medicoSelecionado = m;
    }

    public void selecionarEnfermeiro(Enfermeiro enf) {
        this.enfermeiroSelecionado = enf;
    }

    public void selecionarEspecialidade(Especialidade esp) {
        this.especialidadeSelecionada = esp;
    }

    public void aplicarFiltrosMedico() {
        medicoModel.setFiltroNome(filtroNomeMedico);
        medicoModel.setFiltroStatus(filtroStatusMedico);
    }

    public void limparFiltrosMedico() {
        filtroNomeMedico   = null;
        filtroStatusMedico = null;
        medicoModel.setFiltroNome(null);
        medicoModel.setFiltroStatus(null);
    }

    public void aplicarFiltrosEnfermeiro() {
        enfermeiroModel.setFiltroNome(filtroNomeEnfermeiro);
        enfermeiroModel.setFiltroSetor(filtroSetorEnfermeiro);
        enfermeiroModel.setFiltroStatus(filtroStatusEnfermeiro);
    }

    public void limparFiltrosEnfermeiro() {
        filtroNomeEnfermeiro   = null;
        filtroSetorEnfermeiro  = null;
        filtroStatusEnfermeiro = null;
        enfermeiroModel.setFiltroNome(null);
        enfermeiroModel.setFiltroSetor(null);
        enfermeiroModel.setFiltroStatus(null);
    }

    public void aplicarFiltrosEspecialidade() {
        especialidadeModel.setFiltroNome(filtroNomeEspecialidade);
        especialidadeModel.setFiltroArea(filtroAreaEspecialidade);
        especialidadeModel.setFiltroStatus(filtroStatusEspecialidade);
    }

    public void limparFiltrosEspecialidade() {
        filtroNomeEspecialidade   = null;
        filtroAreaEspecialidade   = null;
        filtroStatusEspecialidade = null;
        especialidadeModel.setFiltroNome(null);
        especialidadeModel.setFiltroArea(null);
        especialidadeModel.setFiltroStatus(null);
    }

    public String formatarArea(String area) {
        if (area == null) return "—";
        switch (area.toUpperCase()) {
            case "CLINICA":     return "Clínica";
            case "CIRURGIA":    return "Cirurgia";
            case "DIAGNOSTICO": return "Diagnóstico";
            default:            return area;
        }
    }

    public String formatarData(LocalDateTime dt) {
        return dt != null ? dt.format(FMT_DT) : "—";
    }

    public String formatarDataNasc(LocalDate dt) {
        return dt != null ? dt.format(FMT_DATA) : "—";
    }

    public void sair() throws IOException {
        beanSessao.encerrarSessao();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
        ec.redirect(ec.getRequestContextPath() + "/logout");
    }

    public MedicoLazyModel getMedicoModel()                { return medicoModel; }
    public EnfermeiroLazyModel getEnfermeiroModel()        { return enfermeiroModel; }
    public EspecialidadeLazyModel getEspecialidadeModel()  { return especialidadeModel; }

    public String getFiltroNomeMedico()    { return filtroNomeMedico; }
    public void setFiltroNomeMedico(String v)    { this.filtroNomeMedico = v; }
    public String getFiltroStatusMedico()  { return filtroStatusMedico; }
    public void setFiltroStatusMedico(String v)  { this.filtroStatusMedico = v; }

    public String getFiltroNomeEnfermeiro()   { return filtroNomeEnfermeiro; }
    public void setFiltroNomeEnfermeiro(String v)   { this.filtroNomeEnfermeiro = v; }
    public String getFiltroSetorEnfermeiro()  { return filtroSetorEnfermeiro; }
    public void setFiltroSetorEnfermeiro(String v)  { this.filtroSetorEnfermeiro = v; }
    public String getFiltroStatusEnfermeiro() { return filtroStatusEnfermeiro; }
    public void setFiltroStatusEnfermeiro(String v) { this.filtroStatusEnfermeiro = v; }

    public long getTotalMedicosAtivos()     { return totalMedicosAtivos; }
    public long getTotalEnfermeirosAtivos() { return totalEnfermeirosAtivos; }
    public long getTotalEspecialidades()    { return totalEspecialidades; }
    public long getTotalAdmins()            { return totalAdmins; }

    public String getFiltroNomeEspecialidade()    { return filtroNomeEspecialidade; }
    public void setFiltroNomeEspecialidade(String v)    { this.filtroNomeEspecialidade = v; }
    public String getFiltroAreaEspecialidade()   { return filtroAreaEspecialidade; }
    public void setFiltroAreaEspecialidade(String v)   { this.filtroAreaEspecialidade = v; }
    public String getFiltroStatusEspecialidade() { return filtroStatusEspecialidade; }
    public void setFiltroStatusEspecialidade(String v) { this.filtroStatusEspecialidade = v; }

    public Medico getMedicoSelecionado()         { return medicoSelecionado; }
    public void setMedicoSelecionado(Medico m)   { this.medicoSelecionado = m; }
    public Enfermeiro getEnfermeiroSelecionado()             { return enfermeiroSelecionado; }
    public void setEnfermeiroSelecionado(Enfermeiro enf)     { this.enfermeiroSelecionado = enf; }
    public Especialidade getEspecialidadeSelecionada()              { return especialidadeSelecionada; }
    public void setEspecialidadeSelecionada(Especialidade esp)      { this.especialidadeSelecionada = esp; }
}
