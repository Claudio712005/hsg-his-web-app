package br.com.hsg.dao;

import br.com.hsg.domain.entity.ContaUsuario;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ContaUsuarioDAO {

    @PersistenceContext(unitName = "defaultPU")
    private EntityManager em;

    public ContaUsuario salvar(ContaUsuario conta) {
        em.persist(conta);
        em.flush();
        return conta;
    }
}
