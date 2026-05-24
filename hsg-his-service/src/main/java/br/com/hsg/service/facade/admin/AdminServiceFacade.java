package br.com.hsg.service.facade.admin;

import br.com.hsg.domain.entity.Admin;

import javax.ejb.Local;

@Local
public interface AdminServiceFacade {

    Admin buscarPorKeycloakId(String keycloakId);

    Admin buscarPorId(Long id);

    long contarAdmins();
}
