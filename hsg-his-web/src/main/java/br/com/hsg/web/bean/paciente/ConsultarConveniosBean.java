package br.com.hsg.web.bean.paciente;

import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.enums.TipoCoberturaPlano;
import br.com.hsg.service.facade.paciente.ConvenioPacienteServiceFacade;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ViewScoped
@Named("consultarConveniosBean")
public class ConsultarConveniosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB private ConvenioPacienteServiceFacade convenioPacienteService;

    private List<Convenio> convenios;
    private Convenio convenioSelecionado;
    private List<PlanoConvenio> planosDoConvenio;
    private String filtroCobertura;
    private PlanoConvenio planoSelecionado;
    private List<RegraCobertura> regrasDoPlanoSelecionado;

    @PostConstruct
    public void init() {
        this.convenios = convenioPacienteService.listarConveniosAtivos();
    }

    public void selecionarConvenio(Convenio c) {
        this.convenioSelecionado       = c;
        this.planosDoConvenio          = convenioPacienteService.listarPlanosAtivosPorConvenio(c.getId());
        this.filtroCobertura           = null;
        this.planoSelecionado          = null;
        this.regrasDoPlanoSelecionado  = Collections.emptyList();
    }

    public List<PlanoConvenio> getPlanosDoSelecionado() {
        if (planosDoConvenio == null) {
            return Collections.emptyList();
        }
        if (filtroCobertura == null || filtroCobertura.isEmpty()) {
            return planosDoConvenio;
        }
        List<PlanoConvenio> filtrados = new ArrayList<>();
        for (PlanoConvenio p : planosDoConvenio) {
            if (p.getTipoCobertura() != null && p.getTipoCobertura().getValor().equals(filtroCobertura)) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    public void selecionarPlano(PlanoConvenio p) {
        this.planoSelecionado         = p;
        this.regrasDoPlanoSelecionado = convenioPacienteService.listarRegrasDoPlano(p.getId());
    }

    public int getQtdPlanos(Long idConvenio) {
        return convenioPacienteService.listarPlanosAtivosPorConvenio(idConvenio).size();
    }

    public String descreverCobertura(PlanoConvenio p) {
        return p != null && p.getTipoCobertura() != null ? p.getTipoCobertura().getDescricao() : "—";
    }

    public TipoCoberturaPlano[] getTiposCobertura() {
        return TipoCoberturaPlano.values();
    }

    public List<Convenio> getConvenios()                     { return convenios; }
    public Convenio getConvenioSelecionado()                 { return convenioSelecionado; }
    public PlanoConvenio getPlanoSelecionado()               { return planoSelecionado; }
    public List<RegraCobertura> getRegrasDoPlanoSelecionado(){ return regrasDoPlanoSelecionado; }
    public String getFiltroCobertura()                       { return filtroCobertura; }
    public void setFiltroCobertura(String v)                 { this.filtroCobertura = v; }
}
