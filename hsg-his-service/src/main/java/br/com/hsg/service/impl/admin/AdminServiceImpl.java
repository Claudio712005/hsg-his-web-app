package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.AdminDAO;
import br.com.hsg.domain.entity.Admin;
import br.com.hsg.service.facade.admin.AdminServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class AdminServiceImpl implements AdminServiceFacade {

    @EJB
    private AdminDAO adminDAO;

    @Override
    public Admin buscarPorKeycloakId(String keycloakId) {
        return adminDAO.buscarPorKeycloakId(keycloakId);
    }

    @Override
    public Admin buscarPorId(Long id) {
        return adminDAO.buscarPorId(id);
    }

    @Override
    public long contarAdmins() {
        return adminDAO.contarTotal();
    }
}
