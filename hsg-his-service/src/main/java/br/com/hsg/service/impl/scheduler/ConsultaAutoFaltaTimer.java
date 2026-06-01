package br.com.hsg.service.impl.scheduler;

import br.com.hsg.service.facade.scheduler.ConsultaAutoFaltaServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Logger;

@Singleton
@Startup
public class ConsultaAutoFaltaTimer {

    private static final Logger LOG = Logger.getLogger(ConsultaAutoFaltaTimer.class.getName());

    @EJB private ConsultaAutoFaltaServiceFacade autoFaltaService;

    @Schedule(hour = "2", minute = "0", second = "0", persistent = false, info = "consulta-auto-falta")
    public void executar() {
        LOG.info("[ConsultaAutoFaltaTimer] Início da execução agendada.");
        int n = autoFaltaService.marcarFaltasAutomaticas();
        LOG.info("[ConsultaAutoFaltaTimer] Fim da execução: " + n + " consulta(s) marcada(s) como falta.");
    }
}
