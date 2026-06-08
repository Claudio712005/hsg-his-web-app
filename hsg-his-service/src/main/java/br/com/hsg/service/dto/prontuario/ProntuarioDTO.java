package br.com.hsg.service.dto.prontuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProntuarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PacienteResumoDTO       paciente;
    private List<AlergiaResumoDTO>  alergias = new ArrayList<>();
    private List<ConsultaResumoDTO> consultas = new ArrayList<>();

    public PacienteResumoDTO getPaciente()                 { return paciente; }
    public void setPaciente(PacienteResumoDTO v)           { this.paciente = v; }
    public List<AlergiaResumoDTO> getAlergias()            { return alergias; }
    public void setAlergias(List<AlergiaResumoDTO> v)      { this.alergias = v; }
    public List<ConsultaResumoDTO> getConsultas()          { return consultas; }
    public void setConsultas(List<ConsultaResumoDTO> v)    { this.consultas = v; }

    public int getTotalConsultas()                         { return consultas == null ? 0 : consultas.size(); }
    public int getTotalAlergias()                          { return alergias == null ? 0 : alergias.size(); }
}
