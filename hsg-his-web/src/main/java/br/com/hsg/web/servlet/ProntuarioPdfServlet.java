package br.com.hsg.web.servlet;

import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.dto.prontuario.ProntuarioDTO;
import br.com.hsg.service.facade.clinica.ProntuarioServiceFacade;
import br.com.hsg.service.impl.clinica.ProntuarioPdfBuilder;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.response.UsuarioClinicaDTO;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = "/prontuario/pdf")
public class ProntuarioPdfServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProntuarioPdfServlet.class.getName());

    @EJB    private ProntuarioServiceFacade prontuarioService;
    @Inject private BeanSessao beanSessao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("idPaciente");
        Long idPacienteAlvo = null;
        if (idParam != null && !idParam.isEmpty()) {
            try {
                idPacienteAlvo = Long.valueOf(idParam);
            } catch (NumberFormatException nfe) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "idPaciente inválido.");
                return;
            }
        }

        Long idResp = resolverIdResponsavel();
        TipoResponsavel tpResp = resolverTipoResponsavel();
        if (idResp == null || tpResp == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada.");
            return;
        }

        if (idPacienteAlvo == null) {
            if (tpResp == TipoResponsavel.PACIENTE) {
                idPacienteAlvo = idResp;
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "idPaciente obrigatório.");
                return;
            }
        }

        try {
            ProntuarioDTO dto = prontuarioService.montarParaPaciente(idPacienteAlvo, idResp, tpResp);
            byte[] bytes = ProntuarioPdfBuilder.build(dto);

            resp.setContentType("application/pdf");
            resp.setContentLength(bytes.length);
            String filename = "prontuario-" + idPacienteAlvo + ".pdf";
            resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
            try (OutputStream out = resp.getOutputStream()) {
                out.write(bytes);
                out.flush();
            }
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.WARNING, "[ProntuarioPdfServlet] {0}", iae.getMessage());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, iae.getMessage());
        } catch (IllegalStateException ise) {
            LOG.log(Level.WARNING, "[ProntuarioPdfServlet] {0}", ise.getMessage());
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, ise.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "[ProntuarioPdfServlet] Falha", ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao gerar PDF.");
        }
    }

    private Long resolverIdResponsavel() {
        if (beanSessao == null) return null;
        if (beanSessao.getPaciente() != null) return beanSessao.getPaciente().getId();
        if (beanSessao.getAdmin() != null)    return beanSessao.getAdmin().getId();
        UsuarioClinicaDTO u = beanSessao.getUsuarioClinica();
        return u != null ? u.getId() : null;
    }

    private TipoResponsavel resolverTipoResponsavel() {
        if (beanSessao == null) return null;
        if (beanSessao.getPaciente() != null) return TipoResponsavel.PACIENTE;
        if (beanSessao.getAdmin() != null)    return TipoResponsavel.ADMIN;
        UsuarioClinicaDTO u = beanSessao.getUsuarioClinica();
        if (u == null || u.getTipo() == null) return null;
        if ("ENFERMEIRO".equalsIgnoreCase(u.getTipo())) return TipoResponsavel.ENFERMEIRO;
        if ("MEDICO".equalsIgnoreCase(u.getTipo()))     return TipoResponsavel.MEDICO;
        return null;
    }
}
