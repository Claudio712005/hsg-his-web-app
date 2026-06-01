package br.com.hsg.service.impl.scheduler;

import br.com.hsg.service.facade.notificacao.NotificacaoServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Logger;

@Singleton
@Startup
public class NotificacaoLimpezaTimer {

    private static final Logger LOG = Logger.getLogger(NotificacaoLimpezaTimer.class.getName());

    @EJB private NotificacaoServiceFacade notificacaoService;

    @Schedule(hour = "3", minute = "0", second = "0", persistent = false, info = "notificacao-limpeza")
    public void executar() {
        LOG.info("[NotificacaoLimpezaTimer] Início da execução agendada.");
        int n = notificacaoService.limparExpiradas();
        LOG.info("[NotificacaoLimpezaTimer] Fim da execução: " + n + " notificação(ões) removida(s).");
    }
}
