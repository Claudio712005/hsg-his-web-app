package br.com.hsg.service.dto.prontuario;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaResumoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long          id;
    private LocalDateTime dataConsulta;
    private String        medicoNome;
    private String        medicoCrm;
    private String        especialidade;
    private String        status;
    private String        tipoAtendimento;
    private String        observacaoClinica;
    private String        motivoCancelamento;

    private List<AnotacaoResumoDTO> anotacoes = new ArrayList<>();
    private List<AnexoResumoDTO>    anexos    = new ArrayList<>();
    private ReceitaResumoDTO        receitaAtiva;
    private List<ReceitaResumoDTO>  receitasInativas = new ArrayList<>();

    public Long getId()                                { return id; }
    public void setId(Long v)                          { this.id = v; }
    public LocalDateTime getDataConsulta()             { return dataConsulta; }
    public void setDataConsulta(LocalDateTime v)       { this.dataConsulta = v; }
    public String getMedicoNome()                      { return medicoNome; }
    public void setMedicoNome(String v)                { this.medicoNome = v; }
    public String getMedicoCrm()                       { return medicoCrm; }
    public void setMedicoCrm(String v)                 { this.medicoCrm = v; }
    public String getEspecialidade()                   { return especialidade; }
    public void setEspecialidade(String v)             { this.especialidade = v; }
    public String getStatus()                          { return status; }
    public void setStatus(String v)                    { this.status = v; }
    public String getTipoAtendimento()                 { return tipoAtendimento; }
    public void setTipoAtendimento(String v)           { this.tipoAtendimento = v; }
    public String getObservacaoClinica()               { return observacaoClinica; }
    public void setObservacaoClinica(String v)         { this.observacaoClinica = v; }
    public String getMotivoCancelamento()              { return motivoCancelamento; }
    public void setMotivoCancelamento(String v)        { this.motivoCancelamento = v; }
    public List<AnotacaoResumoDTO> getAnotacoes()      { return anotacoes; }
    public void setAnotacoes(List<AnotacaoResumoDTO> v){ this.anotacoes = v; }
    public List<AnexoResumoDTO> getAnexos()            { return anexos; }
    public void setAnexos(List<AnexoResumoDTO> v)      { this.anexos = v; }
    public ReceitaResumoDTO getReceitaAtiva()          { return receitaAtiva; }
    public void setReceitaAtiva(ReceitaResumoDTO v)    { this.receitaAtiva = v; }
    public List<ReceitaResumoDTO> getReceitasInativas(){ return receitasInativas; }
    public void setReceitasInativas(List<ReceitaResumoDTO> v) { this.receitasInativas = v; }
}
