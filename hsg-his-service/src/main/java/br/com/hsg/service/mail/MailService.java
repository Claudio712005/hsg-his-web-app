package br.com.hsg.service.mail;

import br.com.hsg.domain.enums.TipoProfissional;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class MailService {

    private static final Logger LOG = Logger.getLogger(MailService.class.getName());

    private static final String BASE_URL  = env("HSG_BASE_URL", "http://localhost:8180/hsg-his");

    private static final String REMETENTE = firstNonBlank(
            env("MAIL_SMTP_FROM", null),
            env("HSG_EMAIL_REMETENTE", null),
            "noreply@hsg-his.com.br");

    private Session jndiSession;

    @PostConstruct
    private void init() {
        try {
            jndiSession = (Session) new InitialContext().lookup("java:jboss/mail/Default");
            LOG.info("[MailService] Sessão JNDI 'java:jboss/mail/Default' carregada com sucesso.");
        } catch (Exception e) {
            jndiSession = null;
            LOG.warning("[MailService] JNDI mail-session não encontrada — e-mails usarão sessão programática via variáveis de ambiente (DEV fallback).");
        }
    }

    public void enviarConviteProfissional(
            String nome,
            String emailPessoal,
            TipoProfissional tipo,
            String token,
            String emailCorporativo) {

        String assunto = "Convite de cadastro — HSG Hospital Information System";
        String corpo   = montarCorpoConvite(nome, tipo, token, emailCorporativo);
        enviar(emailPessoal, assunto, corpo);
    }

    public void enviarConvenioAprovado(
            String nome, String email, String convenio, String plano,
            String carteirinhaMascara, String validade) {

        String assunto = "Convênio aprovado — HSG Hospital Information System";
        String corpo   = montarCorpoConvenioAprovado(nome, convenio, plano, carteirinhaMascara, validade);
        enviar(email, assunto, corpo);
    }

    public void enviarConvenioRejeitado(
            String nome, String email, String convenio, String plano, String motivo) {

        String assunto = "Solicitação de convênio não aprovada — HSG Hospital Information System";
        String corpo   = montarCorpoConvenioRejeitado(nome, convenio, plano, motivo);
        enviar(email, assunto, corpo);
    }

    public void enviarFaltaAutomaticaParaMedico(
            String nomeMedico, String emailMedico,
            String nomePaciente, String dataConsultaFormatada) {

        String assunto = "Consulta marcada como falta automaticamente — HSG Hospital Information System";
        String corpo   = montarCorpoFaltaAutomatica(nomeMedico, nomePaciente, dataConsultaFormatada);
        enviar(emailMedico, assunto, corpo);
    }

    private void enviar(String destinatario, String assunto, String corpo) {
        boolean usandoJndi = (jndiSession != null);
        Session session    = usandoJndi ? jndiSession : buildFallbackSession();

        try {
            MimeMessage mensagem = new MimeMessage(session);
            mensagem.setFrom(new InternetAddress(REMETENTE));
            mensagem.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            mensagem.setSubject(assunto, "UTF-8");
            mensagem.setContent(corpo, "text/html; charset=UTF-8");
            Transport.send(mensagem);
            LOG.info("[MailService] E-mail enviado para " + destinatario
                    + " via " + (usandoJndi ? "JNDI" : "sessão programática") + ".");
        } catch (MessagingException e) {
            LOG.log(Level.SEVERE, "[MailService] Falha ao enviar para " + destinatario, e);
            throw new RuntimeException("Não foi possível enviar o e-mail de convite.", e);
        }
    }

    private Session buildFallbackSession() {
        String  host = env("MAIL_SMTP_HOST",     "localhost");
        String  port = env("MAIL_SMTP_PORT",     "1025");
        String  user = env("MAIL_SMTP_USER",     null);
        String  pass = env("MAIL_SMTP_PASS",     null);
        boolean auth = Boolean.parseBoolean(env("MAIL_SMTP_AUTH",     "false"));
        boolean tls  = Boolean.parseBoolean(env("MAIL_SMTP_STARTTLS", "false"));

        Properties props = new Properties();
        props.put("mail.smtp.host",              host);
        props.put("mail.smtp.port",              port);
        props.put("mail.smtp.auth",              String.valueOf(auth));
        props.put("mail.smtp.starttls.enable",   String.valueOf(tls));
        if (tls) {
            props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        }

        LOG.info("[MailService] Sessão programática: host=" + host + " port=" + port
                + " auth=" + auth + " starttls=" + tls);

        if (auth && user != null && pass != null) {
            final String finalUser = user;
            final String finalPass = pass;
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(finalUser, finalPass);
                }
            });
        }
        return Session.getInstance(props);
    }

    private String montarCorpoConvite(
            String nome, TipoProfissional tipo, String token, String emailCorporativo) {

        String linkAtivacao     = BASE_URL + "/public/ativacao-profissional.xhtml?token=" + token;
        String tipoProfissional = tipo.getDescricao();

        String blocoEmailCorp = emailCorporativo != null
                ? "<div style='background:#f0f7ff;border:1px solid #cce0ff;border-radius:4px;padding:12px 16px;margin:20px 0;'>"
                  + "<p style='margin:0 0 4px;font-size:12px;color:#555;'>Seu e-mail corporativo (login no sistema) será:</p>"
                  + "<p style='margin:0;font-size:15px;font-weight:bold;color:#1a6b8a;font-family:monospace;'>"
                  + emailCorporativo + "</p>"
                  + "</div>"
                : "";

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'/></head>"
                + "<body style='font-family:Arial,sans-serif;color:#333;'>"
                + "<div style='max-width:600px;margin:40px auto;padding:32px;border:1px solid #e0e0e0;border-radius:8px;'>"
                + "<h2 style='color:#1a6b8a;'>Convite de cadastro — HSG HIS</h2>"
                + "<p>Olá, <strong>" + nome + "</strong>.</p>"
                + "<p>Você foi convidado(a) pela administração do sistema HSG para completar seu cadastro como <strong>" + tipoProfissional + "</strong>.</p>"
                + blocoEmailCorp
                + "<p>Clique no botão abaixo para acessar o formulário de cadastro:</p>"
                + "<p style='text-align:center;margin:32px 0;'>"
                + "<a href='" + linkAtivacao + "' style='background:#1a6b8a;color:#fff;padding:12px 28px;border-radius:4px;text-decoration:none;font-weight:bold;'>Completar cadastro</a>"
                + "</p>"
                + "<p style='font-size:12px;color:#888;'>Caso não consiga clicar no botão, copie e cole o link abaixo no seu navegador:<br/>"
                + "<a href='" + linkAtivacao + "' style='color:#1a6b8a;'>" + linkAtivacao + "</a></p>"
                + "<hr style='border:none;border-top:1px solid #eee;margin:24px 0;'/>"
                + "<p style='font-size:11px;color:#aaa;'>Este convite é de uso único e expira em 2 dias. Se você não esperava receber este e-mail, ignore-o.</p>"
                + "</div></body></html>";
    }

    private String montarCorpoConvenioAprovado(
            String nome, String convenio, String plano, String carteirinhaMascara, String validade) {

        String linkConvenio = BASE_URL + "/paciente/meu-convenio.xhtml";
        String validadeTxt  = (validade != null && !validade.trim().isEmpty()) ? validade : "não informada";
        String cartTxt      = (carteirinhaMascara != null && !carteirinhaMascara.trim().isEmpty())
                ? carteirinhaMascara : "—";

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'/></head>"
                + "<body style='font-family:Arial,sans-serif;color:#333;'>"
                + "<div style='max-width:600px;margin:40px auto;padding:32px;border:1px solid #e0e0e0;border-radius:8px;'>"
                + "<h2 style='color:#2e7d32;'>Convênio aprovado &#10003;</h2>"
                + "<p>Olá, <strong>" + nome + "</strong>.</p>"
                + "<p>Sua solicitação de adesão ao convênio foi <strong>aprovada</strong> pela administração do HSG.</p>"
                + "<div style='background:#f1f8f3;border:1px solid #c8e6c9;border-radius:4px;padding:16px;margin:20px 0;'>"
                + "<p style='margin:0 0 6px;'><strong>Convênio:</strong> " + convenio + "</p>"
                + "<p style='margin:0 0 6px;'><strong>Plano:</strong> " + plano + "</p>"
                + "<p style='margin:0 0 6px;'><strong>Carteirinha:</strong> <span style='font-family:monospace;'>" + cartTxt + "</span></p>"
                + "<p style='margin:0;'><strong>Validade:</strong> " + validadeTxt + "</p>"
                + "</div>"
                + "<p>Este convênio agora é o seu plano ativo. Consulte coberturas e carências no portal:</p>"
                + "<p style='text-align:center;margin:32px 0;'>"
                + "<a href='" + linkConvenio + "' style='background:#2e7d32;color:#fff;padding:12px 28px;border-radius:4px;text-decoration:none;font-weight:bold;'>Ver meu convênio</a>"
                + "</p>"
                + "<hr style='border:none;border-top:1px solid #eee;margin:24px 0;'/>"
                + "<p style='font-size:11px;color:#aaa;'>Mensagem automática do HSG HIS. Em caso de dúvida, procure a administração do hospital.</p>"
                + "</div></body></html>";
    }

    private String montarCorpoConvenioRejeitado(
            String nome, String convenio, String plano, String motivo) {

        String linkConvenio = BASE_URL + "/paciente/meu-convenio.xhtml";
        String motivoTxt    = (motivo != null && !motivo.trim().isEmpty()) ? motivo : "não informado";

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'/></head>"
                + "<body style='font-family:Arial,sans-serif;color:#333;'>"
                + "<div style='max-width:600px;margin:40px auto;padding:32px;border:1px solid #e0e0e0;border-radius:8px;'>"
                + "<h2 style='color:#c62828;'>Solicitação não aprovada</h2>"
                + "<p>Olá, <strong>" + nome + "</strong>.</p>"
                + "<p>Sua solicitação de adesão ao convênio <strong>" + convenio + " — " + plano
                + "</strong> não foi aprovada pela administração do HSG.</p>"
                + "<div style='background:#fdecea;border:1px solid #f5b5b0;border-radius:4px;padding:16px;margin:20px 0;'>"
                + "<p style='margin:0 0 4px;font-size:12px;color:#555;'>Motivo informado:</p>"
                + "<p style='margin:0;font-size:15px;color:#c62828;'>" + motivoTxt + "</p>"
                + "</div>"
                + "<p>Você pode revisar os dados e enviar uma nova solicitação pelo portal:</p>"
                + "<p style='text-align:center;margin:32px 0;'>"
                + "<a href='" + linkConvenio + "' style='background:#1a6b8a;color:#fff;padding:12px 28px;border-radius:4px;text-decoration:none;font-weight:bold;'>Acessar portal</a>"
                + "</p>"
                + "<hr style='border:none;border-top:1px solid #eee;margin:24px 0;'/>"
                + "<p style='font-size:11px;color:#aaa;'>Mensagem automática do HSG HIS. Em caso de dúvida, procure a administração do hospital.</p>"
                + "</div></body></html>";
    }

    private static String env(String key, String defaultValue) {
        String val = System.getenv(key);
        return (val != null && !val.trim().isEmpty()) ? val.trim() : defaultValue;
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return "";
    }

    private String montarCorpoFaltaAutomatica(String nomeMedico, String nomePaciente, String dataConsulta) {
        String portal = BASE_URL + "/clinica/notificacoes.xhtml";
        return "<html><body style=\"margin:0;padding:0;background:#f5f7fb;\">"
                + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" "
                + " style=\"background:#f5f7fb;padding:24px 0;\"><tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"560\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" "
                + " style=\"max-width:560px;background:#ffffff;border-radius:12px;overflow:hidden;"
                + " box-shadow:0 2px 6px rgba(0,0,0,.06);font-family:Arial,Helvetica,sans-serif;color:#1f2a37;\">"

                + "<tr><td style=\"background:#1565c0;color:#ffffff;padding:18px 24px;font-size:14px;"
                + " font-weight:700;letter-spacing:.3px;\">HSG Hospital Information System</td></tr>"

                + "<tr><td style=\"padding:24px;\">"
                + "<div style=\"display:inline-block;background:#fef6e0;color:#7a5a00;border:1px solid #f3d574;"
                + " border-radius:999px;padding:4px 12px;font-size:12px;font-weight:700;letter-spacing:.3px;"
                + " text-transform:uppercase;\">Aviso automático</div>"

                + "<h2 style=\"margin:14px 0 6px;font-size:20px;color:#1f2a37;\">Consulta marcada como falta</h2>"
                + "<p style=\"margin:0 0 16px;font-size:14px;color:#3b4350;line-height:1.55;\">"
                + "Dr(a). <b>" + nomeMedico + "</b>, a consulta abaixo permaneceu com status pendente após o "
                + "horário previsto e foi marcada como <b>FALTOU</b> automaticamente pelo sistema."
                + "</p>"

                + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" "
                + " style=\"background:#f7f9fc;border:1px solid #e6e9ef;border-radius:10px;margin:8px 0 20px;\">"
                + "<tr><td style=\"padding:14px 16px;font-size:13px;color:#1f2a37;\">"
                + "<div style=\"margin-bottom:6px;\"><span style=\"color:#7a8694;font-weight:600;\">Paciente: </span>"
                + nomePaciente + "</div>"
                + "<div><span style=\"color:#7a8694;font-weight:600;\">Data/hora: </span>"
                + "<span style=\"font-family:monospace;font-weight:600;\">" + dataConsulta + "</span></div>"
                + "</td></tr></table>"

                + "<p style=\"margin:0 0 18px;font-size:13.5px;color:#3b4350;line-height:1.55;\">"
                + "Se o atendimento foi realizado, ajuste o status diretamente no portal antes que ele afete os "
                + "indicadores operacionais."
                + "</p>"

                + "<table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">"
                + "<tr><td style=\"border-radius:8px;background:#1565c0;\">"
                + "<a href=\"" + portal + "\" "
                + " style=\"display:inline-block;padding:10px 18px;color:#ffffff;text-decoration:none;"
                + " font-size:13px;font-weight:700;letter-spacing:.3px;\">Abrir portal HSG HIS</a>"
                + "</td></tr></table>"

                + "</td></tr>"

                + "<tr><td style=\"background:#fafbfc;color:#7a8694;font-size:11.5px;padding:14px 24px;"
                + " border-top:1px solid #eef0f3;\">"
                + "Este é um e-mail automático. Não responder."
                + "</td></tr>"

                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }
}
