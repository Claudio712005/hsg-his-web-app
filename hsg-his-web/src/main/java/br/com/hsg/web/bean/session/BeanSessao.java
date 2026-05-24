package br.com.hsg.web.bean.session;

import br.com.hsg.web.dto.response.AdminResponseDTO;
import br.com.hsg.web.dto.response.PacienteResponseDTO;
import br.com.hsg.web.dto.response.UsuarioClinicaDTO;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@SessionScoped
@Named("beanSessao")
public class BeanSessao implements Serializable {

    private static final long serialVersionUID = 1L;

    private PacienteResponseDTO paciente;
    private UsuarioClinicaDTO   usuarioClinica;
    private AdminResponseDTO    admin;

    public PacienteResponseDTO getPaciente()           { return paciente; }
    public void setPaciente(PacienteResponseDTO p)     { this.paciente = p; }

    public UsuarioClinicaDTO getUsuarioClinica()        { return usuarioClinica; }
    public void setUsuarioClinica(UsuarioClinicaDTO u)  { this.usuarioClinica = u; }

    public AdminResponseDTO getAdmin()                  { return admin; }
    public void setAdmin(AdminResponseDTO a)            { this.admin = a; }

    public boolean isLogado() {
        return paciente != null || usuarioClinica != null || admin != null;
    }

    public boolean isLogadoComoPaciente() { return paciente != null; }

    public boolean isLogadoComoClinica()  { return usuarioClinica != null; }

    public boolean isLogadoComoAdmin()    { return admin != null; }

    public void encerrarSessao() {
        paciente       = null;
        usuarioClinica = null;
        admin          = null;
    }
}
