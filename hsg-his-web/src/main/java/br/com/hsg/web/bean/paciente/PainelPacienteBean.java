package br.com.hsg.web.bean.paciente;

import br.com.hsg.dao.PainelPacienteDAO;
import br.com.hsg.domain.entity.MedicaoPaciente;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.service.facade.paciente.ConvenioPacienteServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Named("painelPacienteBean")
@RequestScoped
public class PainelPacienteBean {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Inject
    private BeanSessao beanSessao;

    @Inject
    private PainelPacienteDAO painelPacienteDAO;

    @EJB
    private ConvenioPacienteServiceFacade convenioPacienteService;

    private Paciente paciente;
    private MedicaoPaciente ultimaMedicao;
    private PacienteConvenio convenioAtivo;

    @PostConstruct
    public void init() {
        if (beanSessao.getPaciente() == null) {
            return;
        }
        Long id = beanSessao.getPaciente().getId();
        paciente = painelPacienteDAO.buscarPorId(id);
        ultimaMedicao = painelPacienteDAO.buscarUltimaMedicao(id);
        convenioAtivo = convenioPacienteService.buscarConvenioAtivo(id);
    }

    public PacienteConvenio getConvenioAtivo() {
        return convenioAtivo;
    }

    public boolean isPossuiConvenio() {
        return convenioAtivo != null;
    }

    public String getConvenioValidadeFormatada() {
        if (convenioAtivo == null || convenioAtivo.getDataValidade() == null) return "—";
        return convenioAtivo.getDataValidade().format(FMT_DATA);
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public MedicaoPaciente getUltimaMedicao() {
        return ultimaMedicao;
    }

    public String getProntuario() {
        if (paciente == null || paciente.getId() == null) return "N/A";
        return String.format("PRT-%07d", paciente.getId());
    }

    public int getIdade() {
        if (paciente == null || paciente.getDataNascimento() == null) return 0;
        return Period.between(paciente.getDataNascimento(), LocalDate.now()).getYears();
    }

    public String getDataNascimentoFormatada() {
        if (paciente == null || paciente.getDataNascimento() == null) return "";
        return paciente.getDataNascimento().format(FMT_DATA);
    }

    public String getDataCadastroFormatada() {
        if (paciente == null || paciente.getDataCadastro() == null) return "";
        return paciente.getDataCadastro().format(FMT_DATA);
    }

    public String getUltimaMedicaoFormatada() {
        if (ultimaMedicao == null || ultimaMedicao.getDataMedicao() == null) return "";
        return ultimaMedicao.getDataMedicao().format(FMT_DATETIME);
    }

    public String getStatusDescricao() {
        if (paciente == null || paciente.getStatus() == null) return "";
        return paciente.getStatus().getDescricao();
    }

    public String getStatusCssClass() {
        if (paciente == null || paciente.getStatus() == null) return "pendente";
        switch (paciente.getStatus()) {
            case A: return "ativo";
            case I: return "inativo";
            case S: return "suspenso";
            default: return "pendente";
        }
    }

    public void sair() throws IOException {
        beanSessao.encerrarSessao();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
        ec.redirect(ec.getRequestContextPath() + "/home.xhtml");
    }
}