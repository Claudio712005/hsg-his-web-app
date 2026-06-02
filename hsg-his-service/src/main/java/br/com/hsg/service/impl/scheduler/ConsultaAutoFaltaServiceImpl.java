package br.com.hsg.service.impl.scheduler;

import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.dao.ConsultaHistoricoDAO;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.ConsultaHistorico;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.enums.AcaoConsulta;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.facade.notificacao.NotificacaoServiceFacade;
import br.com.hsg.service.facade.scheduler.ConsultaAutoFaltaServiceFacade;
import br.com.hsg.service.mail.MailService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ConsultaAutoFaltaServiceImpl implements ConsultaAutoFaltaServiceFacade {

    private static final Logger LOG = Logger.getLogger(ConsultaAutoFaltaServiceImpl.class.getName());
    private static final int HORAS_TOLERANCIA = 24;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @EJB private ConsultaDAO consultaDAO;
    @EJB private ConsultaHistoricoDAO historicoDAO;
    @EJB private MailService mailService;
    @EJB private NotificacaoServiceFacade notificacaoService;

    @Override
    public int marcarFaltasAutomaticas() {
        LocalDateTime limite = LocalDateTime.now().minusHours(HORAS_TOLERANCIA);
        List<Consulta> pendentes = consultaDAO.listarPendentesAteLimite(limite);

        int processadas = 0;
        for (Consulta c : pendentes) {
            try {
                c.marcarFalta();
                consultaDAO.atualizar(c);
                registrarHistorico(c);
                notificarMedico(c);
                notificarInApp(c);
                processadas++;
            } catch (Exception ex) {
                LOG.log(Level.WARNING,
                        "[ConsultaAutoFaltaServiceImpl] Falha ao processar consulta id=" + c.getId(), ex);
            }
        }
        LOG.info("[ConsultaAutoFaltaServiceImpl] Auto-falta executada: candidatas=" + pendentes.size()
                + ", processadas=" + processadas);
        return processadas;
    }

    private void notificarInApp(Consulta c) {
        String quando = c.getDataConsulta() != null ? c.getDataConsulta().format(FMT) : "—";
        String pacienteNome = c.getPaciente() != null ? c.getPaciente().getNomeCompleto() : "—";
        String medicoNome = c.getMedico() != null ? c.getMedico().getNomeCompleto() : "—";
        try {
            if (c.getMedico() != null) {
                notificacaoService.notificar(
                        TipoDestinatarioNotificacao.MEDICO, c.getMedico().getId(),
                        "Consulta marcada como falta",
                        "A consulta com " + pacienteNome + " em " + quando
                                + " foi marcada como falta pelo sistema. Ajuste manualmente se foi realizada.",
                        TipoNotificacao.ALERTA, CategoriaNotificacao.CONSULTA,
                        "/clinica/notificacoes.xhtml");
            }
            if (c.getPaciente() != null) {
                notificacaoService.notificar(
                        TipoDestinatarioNotificacao.PACIENTE, c.getPaciente().getId(),
                        "Consulta marcada como falta",
                        "Sua consulta com Dr(a). " + medicoNome + " em " + quando
                                + " foi marcada como falta. Em caso de dúvida procure a recepção.",
                        TipoNotificacao.INFO, CategoriaNotificacao.CONSULTA,
                        "/paciente/minhas-consultas.xhtml");
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING,
                    "[ConsultaAutoFaltaServiceImpl] Falha ao gerar notificações in-app id=" + c.getId(), ex);
        }
    }

    private void registrarHistorico(Consulta c) {
        try {
            historicoDAO.salvar(ConsultaHistorico.registrar(
                    c, AcaoConsulta.FALTOU, null, TipoResponsavel.SISTEMA,
                    "Auto-falta após 24h de tolerância"));
        } catch (Exception ex) {
            LOG.log(Level.WARNING,
                    "[ConsultaAutoFaltaServiceImpl] Falha ao registrar histórico", ex);
        }
    }

    private void notificarMedico(Consulta c) {
        Medico m = c.getMedico();
        if (m == null) return;
        String email = m.getEmail();
        if (email == null || email.trim().isEmpty()) {
            LOG.warning("[ConsultaAutoFaltaServiceImpl] Medico sem email para consulta id=" + c.getId());
            return;
        }
        String nomeMedico   = m.getNomeCompleto() != null ? m.getNomeCompleto() : "—";
        String nomePaciente = (c.getPaciente() != null && c.getPaciente().getNomeCompleto() != null)
                ? c.getPaciente().getNomeCompleto() : "—";
        String quando = c.getDataConsulta() != null ? c.getDataConsulta().format(FMT) : "—";
        try {
            mailService.enviarFaltaAutomaticaParaMedico(nomeMedico, email, nomePaciente, quando);
        } catch (Exception ex) {
            LOG.log(Level.WARNING,
                    "[ConsultaAutoFaltaServiceImpl] Falha ao enviar e-mail ao medico id="
                            + m.getId() + " para consulta id=" + c.getId(), ex);
        }
    }
}
