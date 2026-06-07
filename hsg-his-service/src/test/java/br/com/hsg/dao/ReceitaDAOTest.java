package br.com.hsg.dao;

import br.com.hsg.domain.entity.Receita;
import br.com.hsg.domain.enums.IndicativoStatus;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReceitaDAOTest {

    private EntityManager em;
    private ReceitaDAO dao;

    @Before
    public void setUp() throws Exception {
        em = mock(EntityManager.class);
        dao = new ReceitaDAO();
        Field f = ReceitaDAO.class.getDeclaredField("em");
        f.setAccessible(true);
        f.set(dao, em);
    }

    @Test
    public void salvar_devePersistirERetornar() {
        Receita r = mock(Receita.class);
        assertSame(r, dao.salvar(r));
        verify(em).persist(r);
    }

    @Test
    public void atualizar_deveMerge() {
        Receita r = mock(Receita.class);
        Receita merged = mock(Receita.class);
        when(em.merge(r)).thenReturn(merged);
        assertSame(merged, dao.atualizar(r));
    }

    @Test
    public void buscarPorId_null_retornaNull() {
        assertNull(dao.buscarPorId(null));
    }

    @Test
    public void buscarPorId_deveDelegarFind() {
        Receita r = mock(Receita.class);
        when(em.find(Receita.class, 7L)).thenReturn(r);
        assertSame(r, dao.buscarPorId(7L));
    }

    @Test
    public void buscarAtivaPorConsulta_null_retornaNull() {
        assertNull(dao.buscarAtivaPorConsulta(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void buscarAtivaPorConsulta_deveFiltrarStatusA() {
        Receita r = mock(Receita.class);
        TypedQuery<Receita> q = mock(TypedQuery.class);
        when(em.createQuery(
                "SELECT r FROM Receita r " +
                "LEFT JOIN FETCH r.itens " +
                "LEFT JOIN FETCH r.medico m " +
                "LEFT JOIN FETCH m.especialidade " +
                "LEFT JOIN FETCH r.consulta c " +
                "LEFT JOIN FETCH c.paciente " +
                "WHERE r.consulta.id = :idc AND r.status = :st",
                Receita.class)).thenReturn(q);
        when(q.setParameter(eq("idc"), eq(45L))).thenReturn(q);
        when(q.setParameter(eq("st"), eq(IndicativoStatus.A))).thenReturn(q);
        when(q.getSingleResult()).thenReturn(r);
        assertSame(r, dao.buscarAtivaPorConsulta(45L));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void buscarAtivaPorConsulta_naoExiste_retornaNull() {
        TypedQuery<Receita> q = mock(TypedQuery.class);
        when(em.createQuery(
                "SELECT r FROM Receita r " +
                "LEFT JOIN FETCH r.itens " +
                "LEFT JOIN FETCH r.medico m " +
                "LEFT JOIN FETCH m.especialidade " +
                "LEFT JOIN FETCH r.consulta c " +
                "LEFT JOIN FETCH c.paciente " +
                "WHERE r.consulta.id = :idc AND r.status = :st",
                Receita.class)).thenReturn(q);
        when(q.setParameter(eq("idc"), eq(45L))).thenReturn(q);
        when(q.setParameter(eq("st"), eq(IndicativoStatus.A))).thenReturn(q);
        when(q.getSingleResult()).thenThrow(new NoResultException());
        assertNull(dao.buscarAtivaPorConsulta(45L));
    }
}
