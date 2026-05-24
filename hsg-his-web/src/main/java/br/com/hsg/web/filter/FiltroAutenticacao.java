package br.com.hsg.web.filter;

import br.com.hsg.dao.PainelPacienteDAO;
import br.com.hsg.domain.entity.Admin;
import br.com.hsg.domain.entity.Enfermeiro;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.TipoSanguineo;
import br.com.hsg.service.facade.admin.AdminServiceFacade;
import br.com.hsg.service.facade.clinica.ClinicaServiceFacade;
import br.com.hsg.service.facade.paciente.PacienteServiceFacade;
import br.com.hsg.web.bean.session.BeanSessao;
import br.com.hsg.web.dto.response.AdminResponseDTO;
import br.com.hsg.web.dto.response.PacienteResponseDTO;
import br.com.hsg.web.dto.response.UsuarioClinicaDTO;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.UUID;

@WebFilter("/*")
public class FiltroAutenticacao implements Filter {

    private static final String ATTR_STATE        = "oauth2_state";
    private static final String ATTR_ORIGINAL_URL = "original_url";

    @Inject private BeanSessao            beanSessao;
    @Inject private PacienteServiceFacade pacienteService;
    @Inject private PainelPacienteDAO     painelPacienteDAO;
    @Inject private ClinicaServiceFacade  clinicaService;
    @Inject private AdminServiceFacade    adminService;

    private String kcAuthUrl;
    private String kcLogoutUrl;
    private String kcTokenUrl;
    private String clientId;
    private String redirectUri;
    private String postLogoutRedirectUri;

    @Override
    public void init(FilterConfig config) {
        kcAuthUrl   = envOr("KC_AUTH_URL",
                "http://localhost:8080/realms/hsg-his/protocol/openid-connect/auth");
        kcLogoutUrl = envOr("KC_LOGOUT_URL",
                "http://localhost:8080/realms/hsg-his/protocol/openid-connect/logout");
        kcTokenUrl  = envOr("KC_TOKEN_URL",
                "http://keycloak:8080/realms/hsg-his/protocol/openid-connect/token");
        clientId    = envOr("KC_CLIENT_ID",   "hsg-his-web");
        redirectUri = envOr("KC_REDIRECT_URI",
                "http://localhost:8180/hsg-his/callback");
        postLogoutRedirectUri = envOr("KC_POST_LOGOUT_REDIRECT_URI",
                "http://localhost:8180/hsg-his/");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getServletPath();

        if ("/callback".equals(path)) {
            handleCallback(request, response);
            return;
        }

        if ("/logout".equals(path)) {
            HttpSession sessionToKill = request.getSession(false);
            String idToken = null;
            if (sessionToKill != null) {
                idToken = (String) sessionToKill.getAttribute("kc_id_token");
                sessionToKill.invalidate();
            }
            StringBuilder logoutUrl = new StringBuilder(kcLogoutUrl)
                    .append("?client_id=").append(URLEncoder.encode(clientId, "UTF-8"))
                    .append("&post_logout_redirect_uri=")
                    .append(URLEncoder.encode(postLogoutRedirectUri, "UTF-8"));
            if (idToken != null && !idToken.isEmpty()) {
                logoutUrl.append("&id_token_hint=").append(URLEncoder.encode(idToken, "UTF-8"));
            }
            response.sendRedirect(logoutUrl.toString());
            return;
        }

        if (isPublic(path)) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession existingSession = request.getSession(false);
        if (existingSession != null && beanSessao.isLogado()) {
            if (path.startsWith("/admin/") && !beanSessao.isLogadoComoAdmin()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Acesso restrito a administradores.");
                return;
            }
            if (path.startsWith("/clinica/") && !beanSessao.isLogadoComoClinica()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Acesso restrito à equipe clínica.");
                return;
            }
            if (path.startsWith("/paciente/") && !beanSessao.isLogadoComoPaciente()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Acesso restrito ao portal do paciente.");
                return;
            }
            chain.doFilter(req, res);
            return;
        }

        String state = UUID.randomUUID().toString();
        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_STATE, state);
        session.setAttribute(ATTR_ORIGINAL_URL, request.getRequestURI());

        response.sendRedirect(
                kcAuthUrl
                + "?response_type=code"
                + "&client_id="    + URLEncoder.encode(clientId,    "UTF-8")
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8")
                + "&state="        + URLEncoder.encode(state,       "UTF-8")
                + "&scope=openid");
    }

    private void handleCallback(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        HttpSession session = req.getSession(false);
        String code  = req.getParameter("code");
        String state = req.getParameter("state");

        if (session == null || code == null || code.isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros de callback ausentes.");
            return;
        }

        String expectedState = (String) session.getAttribute(ATTR_STATE);
        if (expectedState == null || !expectedState.equals(state)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "State OAuth2 inválido.");
            return;
        }
        session.removeAttribute(ATTR_STATE);

        String tokenResponse = exchangeCode(code);
        String accessToken   = extractJsonField(tokenResponse, "access_token");
        String idToken       = extractJsonField(tokenResponse, "id_token");
        String keycloakId    = extractSub(accessToken);

        if (idToken != null && !idToken.isEmpty()) {
            session.setAttribute("kc_id_token", idToken);
        }

        if (keycloakId == null || keycloakId.isEmpty()) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido.");
            return;
        }

        String redirectHome;

        if (hasRole(accessToken, "hsg-admin")) {
            redirectHome = handleAdmin(req, res, keycloakId);
        } else if (hasRole(accessToken, "hsg-paciente")) {
            redirectHome = handlePaciente(req, res, keycloakId);
        } else if (hasRole(accessToken, "hsg-medico")) {
            redirectHome = handleMedico(req, res, keycloakId);
        } else if (hasRole(accessToken, "hsg-enfermeiro")) {
            redirectHome = handleEnfermeiro(req, res, keycloakId);
        } else {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Perfil sem acesso ao sistema.");
            return;
        }

        if (redirectHome == null) return;

        String originalUrl = (String) session.getAttribute(ATTR_ORIGINAL_URL);
        session.removeAttribute(ATTR_ORIGINAL_URL);

        if (originalUrl == null || originalUrl.isEmpty() || originalUrl.endsWith("/callback")) {
            originalUrl = redirectHome;
        }

        res.sendRedirect(originalUrl);
    }

    private String handleAdmin(HttpServletRequest req, HttpServletResponse res, String keycloakId)
            throws IOException {

        Admin admin = adminService.buscarPorKeycloakId(keycloakId);
        if (admin == null || !admin.podeLogar()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Administrador sem acesso ao sistema.");
            return null;
        }

        AdminResponseDTO dto = new AdminResponseDTO();
        dto.setId(admin.getId());
        dto.setNomeCompleto(admin.getNomeCompleto());
        dto.setEmail(admin.getEmail());
        dto.setUsername(admin.getContaUsuario().getUsername());

        beanSessao.setAdmin(dto);
        return req.getContextPath() + "/home.xhtml";
    }

    private String handlePaciente(HttpServletRequest req, HttpServletResponse res, String keycloakId)
            throws IOException {

        Paciente paciente = pacienteService.buscarPorKeycloakId(keycloakId);
        if (paciente == null || !paciente.podeLogar()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Paciente sem acesso ao sistema.");
            return null;
        }

        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(paciente.getId());
        dto.setNomeCompleto(paciente.getNomeCompleto());
        dto.setEmail(paciente.getEmail());
        dto.setTelefone(paciente.getTelefone());
        dto.setUsername(paciente.getContaUsuario().getUsername());

        TipoSanguineo tipoSanguineo = painelPacienteDAO.buscarUltimoTipoSanguineo(paciente.getId());
        if (tipoSanguineo != null) {
            dto.setTipoSanguineo(tipoSanguineo.getTipoSanguineo());
        }

        beanSessao.setPaciente(dto);
        return req.getContextPath() + "/home.xhtml";
    }

    private String handleMedico(HttpServletRequest req, HttpServletResponse res, String keycloakId)
            throws IOException {

        Medico medico = clinicaService.buscarMedicoPorKeycloakId(keycloakId);
        if (medico == null || !medico.podeLogar()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Médico sem acesso ao sistema.");
            return null;
        }

        UsuarioClinicaDTO dto = new UsuarioClinicaDTO();
        dto.setId(medico.getId());
        dto.setNomeCompleto(medico.getNomeCompleto());
        dto.setEmail(medico.getEmail());
        dto.setUsername(medico.getContaUsuario().getUsername());
        dto.setTipo("MEDICO");
        dto.setRegistro(medico.getCrm() != null ? medico.getCrm().getFormatado() : null);
        dto.setEspecialidade(medico.getEspecialidade() != null ? medico.getEspecialidade().getNome() : null);

        beanSessao.setUsuarioClinica(dto);
        return req.getContextPath() + "/home.xhtml";
    }

    private String handleEnfermeiro(HttpServletRequest req, HttpServletResponse res, String keycloakId)
            throws IOException {

        Enfermeiro enfermeiro = clinicaService.buscarEnfermeiroPorKeycloakId(keycloakId);
        if (enfermeiro == null || !enfermeiro.podeLogar()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Enfermeiro sem acesso ao sistema.");
            return null;
        }

        UsuarioClinicaDTO dto = new UsuarioClinicaDTO();
        dto.setId(enfermeiro.getId());
        dto.setNomeCompleto(enfermeiro.getNomeCompleto());
        dto.setEmail(enfermeiro.getEmail());
        dto.setUsername(enfermeiro.getContaUsuario().getUsername());
        dto.setTipo("ENFERMEIRO");
        dto.setRegistro(enfermeiro.getCoren() != null ? enfermeiro.getCoren().getFormatado() : null);
        dto.setEspecialidade(enfermeiro.getEspecialidade());

        beanSessao.setUsuarioClinica(dto);
        return req.getContextPath() + "/home.xhtml";
    }

    private boolean isPublic(String path) {
        return path.startsWith("/javax.faces.resource/")
            || path.startsWith("/resources/")
            || path.startsWith("/public/")
            || path.startsWith("/error/");
    }

    private String exchangeCode(String code) throws IOException {
        URL url = new URL(kcTokenUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5_000);
        conn.setReadTimeout(10_000);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String body = "grant_type=authorization_code"
                + "&client_id="    + URLEncoder.encode(clientId,    "UTF-8")
                + "&code="         + URLEncoder.encode(code,        "UTF-8")
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("UTF-8"));
        }

        int status = conn.getResponseCode();
        InputStream stream = (status >= 200 && status < 300)
                ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private String extractJsonField(String json, String field) {
        if (json == null || json.isEmpty()) return null;
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            JsonObject obj = reader.readObject();
            return obj.containsKey(field) ? obj.getString(field) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractSub(String jwt) {
        if (jwt == null) return null;
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;
            String encoded = parts[1];
            switch (encoded.length() % 4) {
                case 2: encoded += "=="; break;
                case 3: encoded += "=";  break;
                default: break;
            }
            String payload = new String(Base64.getUrlDecoder().decode(encoded), "UTF-8");
            return extractJsonField(payload, "sub");
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasRole(String jwt, String role) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return false;
            String encoded = parts[1];
            switch (encoded.length() % 4) {
                case 2: encoded += "=="; break;
                case 3: encoded += "=";  break;
                default: break;
            }
            String payload = new String(Base64.getUrlDecoder().decode(encoded), "UTF-8");
            try (JsonReader reader = Json.createReader(new StringReader(payload))) {
                JsonObject obj = reader.readObject();
                if (!obj.containsKey("realm_access")) return false;
                JsonObject realmAccess = obj.getJsonObject("realm_access");
                if (!realmAccess.containsKey("roles")) return false;
                JsonArray roles = realmAccess.getJsonArray("roles");
                for (int i = 0; i < roles.size(); i++) {
                    if (role.equals(roles.getString(i))) return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static String envOr(String key, String defaultValue) {
        String v = System.getenv(key);
        return (v != null && !v.isEmpty()) ? v : defaultValue;
    }

    @Override
    public void destroy() {}
}
