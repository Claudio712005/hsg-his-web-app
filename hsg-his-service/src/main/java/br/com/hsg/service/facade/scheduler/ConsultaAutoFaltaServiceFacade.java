package br.com.hsg.service.facade.scheduler;

import javax.ejb.Local;

@Local
public interface ConsultaAutoFaltaServiceFacade {

    int marcarFaltasAutomaticas();
}
