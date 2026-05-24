package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.AdminDAO;
import br.com.hsg.domain.entity.Admin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AdminServiceImplTest {

    @Mock private AdminDAO adminDAO;
    @InjectMocks private AdminServiceImpl service;

    @Test
    public void buscarPorKeycloakId_deveDelegarAoDAO() {
        Admin admin = mock(Admin.class);
        when(adminDAO.buscarPorKeycloakId("kc")).thenReturn(admin);
        assertSame(admin, service.buscarPorKeycloakId("kc"));
    }

    @Test
    public void buscarPorId_deveDelegarAoDAO() {
        Admin admin = mock(Admin.class);
        when(adminDAO.buscarPorId(1L)).thenReturn(admin);
        assertSame(admin, service.buscarPorId(1L));
    }

    @Test
    public void contarAdmins_deveDelegarAoDAO() {
        when(adminDAO.contarTotal()).thenReturn(4L);
        assertEquals(4L, service.contarAdmins());
    }
}
