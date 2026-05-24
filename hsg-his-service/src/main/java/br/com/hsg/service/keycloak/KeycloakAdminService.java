package br.com.hsg.service.keycloak;

import javax.ejb.Stateless;
import javax.json.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class KeycloakAdminService {

    private static final Logger LOG = Logger.getLogger(KeycloakAdminService.class.getName());

    public String criarUsuario(String username,
                               String email,
                               String firstName,
                               String lastName) {

        String token = obterTokenAdmin();
        String url   = baseUrl() + "/admin/realms/" + realm() + "/users";

        JsonObject body = Json.createObjectBuilder()
                .add("username",        username)
                .add("email",           email)
                .add("firstName",       firstName)
                .add("lastName",        lastName)
                .add("enabled",         true)
                .add("emailVerified",   true)
                .add("requiredActions", Json.createArrayBuilder().build())
                .build();

        HttpURLConnection conn = null;
        try {
            conn = conexao(url, "POST", token);
            escrever(conn, body.toString());
            int status = conn.getResponseCode();
            if (status != 201) {
                throw new RuntimeException("Keycloak: falha ao criar usuário (HTTP "
                        + status + "): " + ler(conn.getErrorStream()));
            }
            String location = conn.getHeaderField("Location");
            if (location == null || !location.contains("/")) {
                throw new RuntimeException("Keycloak: header Location ausente na resposta de criação de usuário.");
            }
            String keycloakId = location.substring(location.lastIndexOf('/') + 1);
            LOG.info("[KeycloakAdminService] Usuário criado: " + username + " → " + keycloakId);
            return keycloakId;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Keycloak: erro ao criar usuário.", e);
        } finally {
            fechar(conn);
        }
    }

    public void definirSenha(String keycloakUserId, String senha) {
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Keycloak: tentativa de definir senha vazia para o usuário " + keycloakUserId);
        }
        LOG.info("[KeycloakAdminService] Definindo senha para usuário " + keycloakUserId
                + " (tamanho=" + senha.length() + " chars)");

        String token = obterTokenAdmin();
        String url   = baseUrl() + "/admin/realms/" + realm()
                + "/users/" + keycloakUserId + "/reset-password";

        JsonObject body = Json.createObjectBuilder()
                .add("type",      "password")
                .add("value",     senha)
                .add("temporary", false)
                .build();

        HttpURLConnection conn = null;
        try {
            conn = conexao(url, "PUT", token);
            escrever(conn, body.toString());
            int status = conn.getResponseCode();
            if (status != 204) {
                throw new RuntimeException("Keycloak: falha ao definir senha (HTTP " + status
                        + "): " + ler(conn.getErrorStream()));
            }
            LOG.info("[KeycloakAdminService] Senha definida com sucesso para " + keycloakUserId);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Keycloak: erro ao definir senha.", e);
        } finally {
            fechar(conn);
        }
    }

    public void atribuirRole(String keycloakUserId, String roleNome) {
        String token = obterTokenAdmin();
        JsonObject role = buscarRole(token, roleNome);
        String url = baseUrl() + "/admin/realms/" + realm()
                + "/users/" + keycloakUserId + "/role-mappings/realm";

        HttpURLConnection conn = null;
        try {
            conn = conexao(url, "POST", token);
            escrever(conn, Json.createArrayBuilder().add(role).build().toString());
            int status = conn.getResponseCode();
            if (status != 204) {
                throw new RuntimeException("Keycloak: falha ao atribuir role '"
                        + roleNome + "' (HTTP " + status + "): " + ler(conn.getErrorStream()));
            }
            LOG.info("[KeycloakAdminService] Role '" + roleNome + "' atribuída a " + keycloakUserId);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Keycloak: erro ao atribuir role.", e);
        } finally {
            fechar(conn);
        }
    }

    public void tentarRemoverUsuario(String keycloakUserId) {
        try {
            String token = obterTokenAdmin();
            String url = baseUrl() + "/admin/realms/" + realm() + "/users/" + keycloakUserId;
            HttpURLConnection conn = conexao(url, "DELETE", token);
            conn.getResponseCode();
            conn.disconnect();
            LOG.warning("[KeycloakAdminService] Usuário removido como compensação: " + keycloakUserId);
        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                    "[KeycloakAdminService] ATENÇÃO: falha na compensação — usuário Keycloak órfão: "
                            + keycloakUserId + ". Remover manualmente.", e);
        }
    }

    private String obterTokenAdmin() {
        String url = baseUrl() + "/realms/master/protocol/openid-connect/token";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5_000);
            conn.setReadTimeout(10_000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "grant_type=password"
                    + "&client_id=admin-cli"
                    + "&username=" + URLEncoder.encode(adminUser(), "UTF-8")
                    + "&password=" + URLEncoder.encode(adminPass(), "UTF-8");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Keycloak: falha ao obter token admin (HTTP " + status + ").");
            }
            String response = ler(conn.getInputStream());
            String token = extrair(response, "access_token");
            if (token == null || token.isEmpty()) {
                throw new RuntimeException("Keycloak: access_token ausente na resposta de autenticação admin.");
            }
            return token;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Keycloak: erro ao autenticar admin.", e);
        } finally {
            fechar(conn);
        }
    }

    private JsonObject buscarRole(String token, String roleNome) {
        String url = baseUrl() + "/admin/realms/" + realm() + "/roles/" + roleNome;
        HttpURLConnection conn = null;
        try {
            conn = conexao(url, "GET", token);
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Keycloak: role '" + roleNome
                        + "' não encontrada (HTTP " + status + "). Verifique se foi criada no realm.");
            }
            try (JsonReader r = Json.createReader(new StringReader(ler(conn.getInputStream())))) {
                return r.readObject();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Keycloak: erro ao buscar role.", e);
        } finally {
            fechar(conn);
        }
    }

    private HttpURLConnection conexao(String url, String method, String token) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(5_000);
        conn.setReadTimeout(10_000);
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Content-Type", "application/json");
        if (!"GET".equals(method) && !"DELETE".equals(method)) {
            conn.setDoOutput(true);
        }
        return conn;
    }

    private void escrever(HttpURLConnection conn, String json) throws IOException {
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String ler(InputStream stream) throws IOException {
        if (stream == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private String extrair(String json, String field) {
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            JsonObject obj = reader.readObject();
            return obj.containsKey(field) ? obj.getString(field) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void fechar(HttpURLConnection conn) {
        if (conn != null) conn.disconnect();
    }

    private String env(String key, String def) {
        String v = System.getenv(key);
        return (v != null && !v.isEmpty()) ? v : def;
    }

    private String baseUrl() {
        return env("KC_ADMIN_BASE_URL", "http://keycloak:8080");
    }

    private String realm() {
        return env("KC_REALM", "hsg-his");
    }

    private String adminUser() {
        return env("KC_ADMIN_USER", "admin");
    }

    private String adminPass() {
        return env("KC_ADMIN_PASS", "Admin@HSG2026");
    }
}
