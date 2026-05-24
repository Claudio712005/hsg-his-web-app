package br.com.hsg.web.bean.admin;

import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.domain.enums.TipoCoberturaPlano;
import br.com.hsg.service.facade.admin.ConvenioServiceFacade;
import br.com.hsg.service.facade.admin.RelatorioConvenioServiceFacade;
import br.com.hsg.web.dto.response.ContagemConvenioDTO;
import br.com.hsg.web.model.VinculoConvenioLazyModel;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@Named("relatorioConvenioBean")
public class RelatorioConvenioBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT_DT   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String SERIES_COLORS =
            "5B8FF9,61DDAA,65789B,F6BD16,7262FD,78D3F8,9661BC,F6903D,008685,F08BB4";

    @EJB private RelatorioConvenioServiceFacade relatorioService;
    @EJB private ConvenioServiceFacade          convenioService;

    private long totalVinculos;
    private long totalConvenios;
    private long solicitacoesPendentes;
    private BigDecimal receitaMensal;
    private String planoMaisPopular;
    private long planoMaisPopularQtd;

    private List<ContagemConvenioDTO> contagens;

    private PieChartModel pieModel;
    private String filtroCoberturaGrafico;

    private BarChartModel barModel;
    private Long convenioGraficoId;

    private VinculoConvenioLazyModel model;
    private String filtroPaciente;
    private Long filtroConvenioId;

    @PostConstruct
    public void init() {
        carregarKpis();
        carregarContagens();

        model = new VinculoConvenioLazyModel(relatorioService);
        model.aplicarFiltros(null, null);

        montarPieModel();

        List<Convenio> convenios = getConveniosParaSelect();
        if (!convenios.isEmpty()) {
            convenioGraficoId = convenios.get(0).getId();
        }
        montarBarModel();
    }

    private void carregarKpis() {
        this.totalVinculos         = relatorioService.contarTotalVinculosAtivos();
        this.solicitacoesPendentes = relatorioService.contarSolicitacoesPendentes();
        this.receitaMensal         = relatorioService.somaReceitaMensalAtiva();

        List<Object[]> topPlanos = relatorioService.contarTopPlanos(1);
        if (!topPlanos.isEmpty()) {
            this.planoMaisPopular    = (String) topPlanos.get(0)[0];
            this.planoMaisPopularQtd = ((Number) topPlanos.get(0)[1]).longValue();
        } else {
            this.planoMaisPopular    = "—";
            this.planoMaisPopularQtd = 0;
        }
    }

    private void carregarContagens() {
        this.contagens = new ArrayList<>();
        for (Object[] linha : relatorioService.contarVinculosPorConvenio()) {
            String nome   = (String) linha[0];
            long total    = ((Number) linha[1]).longValue();
            long planos   = ((Number) linha[2]).longValue();
            BigDecimal rc = (linha[3] instanceof BigDecimal)
                    ? (BigDecimal) linha[3] : new BigDecimal(linha[3].toString());
            contagens.add(new ContagemConvenioDTO(nome, total, planos, rc));
        }
        this.totalConvenios = contagens.size();
    }

    private void montarPieModel() {
        PieChartModel m = new PieChartModel();
        TipoCoberturaPlano filtro = filtroCoberturaGrafico != null && !filtroCoberturaGrafico.isEmpty()
                ? TipoCoberturaPlano.fromValor(filtroCoberturaGrafico) : null;
        List<Object[]> dados = relatorioService.contarVinculosPorConvenio(filtro);
        for (Object[] linha : dados) {
            m.set((String) linha[0], ((Number) linha[1]).longValue());
        }
        m.setTitle("Distribuição de pacientes por convênio");
        m.setLegendPosition("e");
        m.setShowDataLabels(true);
        m.setDataFormat("value");
        m.setShowDatatip(true);
        m.setSeriesColors(SERIES_COLORS);
        m.setShadow(false);
        this.pieModel = m;
    }

    private void montarBarModel() {
        BarChartModel m = new BarChartModel();
        ChartSeries serie = new ChartSeries();
        serie.setLabel("Pacientes ativos");

        if (convenioGraficoId != null) {
            for (Object[] linha : relatorioService.contarVinculosPorPlano(convenioGraficoId)) {
                serie.set((String) linha[0], ((Number) linha[1]).longValue());
            }
        }
        m.addSeries(serie);
        m.setTitle("Pacientes por plano");
        m.setLegendPosition("ne");
        m.setAnimate(true);
        m.setShowDatatip(true);
        m.setSeriesColors(SERIES_COLORS);
        m.setShadow(false);

        Axis x = m.getAxis(AxisType.X);
        x.setLabel("Plano");
        Axis y = m.getAxis(AxisType.Y);
        y.setLabel("Pacientes");
        y.setMin(0);

        this.barModel = m;
    }

    public void onChangeFiltroGrafico() {
        montarPieModel();
    }

    public void onChangeConvenioGrafico() {
        montarBarModel();
    }

    public void aplicarFiltros() {
        model.aplicarFiltros(filtroPaciente, filtroConvenioId);
    }

    public void limparFiltros() {
        this.filtroPaciente   = null;
        this.filtroConvenioId = null;
        model.aplicarFiltros(null, null);
    }

    public List<Convenio> getConveniosParaSelect() {
        return convenioService.listarConveniosAtivos();
    }

    public TipoCoberturaPlano[] getTiposCobertura() {
        return TipoCoberturaPlano.values();
    }

    public String getNomeConvenioGrafico() {
        if (convenioGraficoId == null) return "—";
        for (Convenio c : getConveniosParaSelect()) {
            if (c.getId().equals(convenioGraficoId)) return c.getNome();
        }
        return "—";
    }

    public String formatarData(LocalDateTime dt) {
        return dt != null ? dt.format(FMT_DT) : "—";
    }

    public String formatarDataSimples(LocalDate d) {
        return d != null ? d.format(FMT_DATA) : "—";
    }

    public long getTotalVinculos()          { return totalVinculos; }
    public long getTotalConvenios()         { return totalConvenios; }
    public long getSolicitacoesPendentes()  { return solicitacoesPendentes; }
    public BigDecimal getReceitaMensal()    { return receitaMensal; }
    public String getPlanoMaisPopular()     { return planoMaisPopular; }
    public long getPlanoMaisPopularQtd()    { return planoMaisPopularQtd; }

    public List<ContagemConvenioDTO> getContagens() { return contagens; }

    public PieChartModel getPieModel()      { return pieModel; }
    public BarChartModel getBarModel()      { return barModel; }

    public String getFiltroCoberturaGrafico()        { return filtroCoberturaGrafico; }
    public void setFiltroCoberturaGrafico(String v)  { this.filtroCoberturaGrafico = v; }
    public Long getConvenioGraficoId()               { return convenioGraficoId; }
    public void setConvenioGraficoId(Long v)         { this.convenioGraficoId = v; }

    public VinculoConvenioLazyModel getModel()       { return model; }
    public String getFiltroPaciente()                { return filtroPaciente; }
    public void setFiltroPaciente(String v)          { this.filtroPaciente = v; }
    public Long getFiltroConvenioId()                { return filtroConvenioId; }
    public void setFiltroConvenioId(Long v)          { this.filtroConvenioId = v; }
}
