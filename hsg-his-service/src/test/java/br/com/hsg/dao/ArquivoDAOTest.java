package br.com.hsg.dao;

import br.com.hsg.domain.entity.Arquivo;
import br.com.hsg.domain.enums.IndicativoStatus;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArquivoDAOTest {

    private EntityManager em;
    private ArquivoDAO dao;

    @Before
    public void setUp() throws Exception {
        em = mock(EntityManager.class);
        dao = new ArquivoDAO();
        Field f = ArquivoDAO.class.getDeclaredField("em");
        f.setAccessible(true);
        f.set(dao, em);
    }

    @Test
    public void salvar_devePersistirERetornar() {
        Arquivo a = mock(Arquivo.class);
        assertSame(a, dao.salvar(a));
        verify(em).persist(a);
    }

    @Test
    public void atualizar_deveMerge() {
        Arquivo a = mock(Arquivo.class);
        Arquivo merged = mock(Arquivo.class);
        when(em.merge(a)).thenReturn(merged);
        assertSame(merged, dao.atualizar(a));
    }

    @Test
    public void buscarPorId_null_retornaNull() {
        assertNull(dao.buscarPorId(null));
    }

    @Test
    public void buscarPorId_deveDelegarParaFind() {
        Arquivo a = mock(Arquivo.class);
        when(em.find(Arquivo.class, 7L)).thenReturn(a);
        assertSame(a, dao.buscarPorId(7L));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void buscarPorPathLogico_existe() {
        Arquivo a = mock(Arquivo.class);
        TypedQuery<Arquivo> q = mock(TypedQuery.class);
        when(em.createQuery(
                "SELECT a FROM Arquivo a WHERE a.pathLogico = :p", Arquivo.class)).thenReturn(q);
        when(q.setParameter("p", "/x")).thenReturn(q);
        when(q.getSingleResult()).thenReturn(a);
        assertSame(a, dao.buscarPorPathLogico("/x"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void buscarPorPathLogico_naoExiste_retornaNull() {
        TypedQuery<Arquivo> q = mock(TypedQuery.class);
        when(em.createQuery(
                "SELECT a FROM Arquivo a WHERE a.pathLogico = :p", Arquivo.class)).thenReturn(q);
        when(q.setParameter("p", "/x")).thenReturn(q);
        when(q.getSingleResult()).thenThrow(new NoResultException());
        assertNull(dao.buscarPorPathLogico("/x"));
    }

    @Test
    public void buscarPorPathLogico_null_retornaNull() {
        assertNull(dao.buscarPorPathLogico(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void listarPorConsulta_deveFiltrarPorAtivos() {
        Arquivo a = mock(Arquivo.class);
        TypedQuery<Arquivo> q = mock(TypedQuery.class);
        when(em.createQuery(
                "SELECT a FROM Arquivo a " +
                "WHERE a.idConsulta = :idc AND a.status = :st " +
                "ORDER BY a.dataUpload DESC", Arquivo.class)).thenReturn(q);
        when(q.setParameter(eq("idc"), eq(45L))).thenReturn(q);
        when(q.setParameter(eq("st"), eq(IndicativoStatus.A))).thenReturn(q);
        when(q.getResultList()).thenReturn(Arrays.asList(a));
        List<Arquivo> out = dao.listarPorConsulta(45L);
        assertEquals(1, out.size());
        assertSame(a, out.get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void listarPorAnotacao_deveFiltrarPorAtivos() {
        TypedQuery<Arquivo> q = mock(TypedQuery.class);
        when(em.createQuery(
                "SELECT a FROM Arquivo a " +
                "WHERE a.idAnotacao = :ida AND a.status = :st " +
                "ORDER BY a.dataUpload DESC", Arquivo.class)).thenReturn(q);
        when(q.setParameter(eq("ida"), eq(77L))).thenReturn(q);
        when(q.setParameter(eq("st"), eq(IndicativoStatus.A))).thenReturn(q);
        when(q.getResultList()).thenReturn(Collections.emptyList());
        assertEquals(0, dao.listarPorAnotacao(77L).size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void listarPorPaciente_deveFiltrarPorAtivos() {
        TypedQuery<Arquivo> q = mock(TypedQuery.class);
        when(em.createQuery(
                "SELECT a FROM Arquivo a " +
                "WHERE a.idPaciente = :idp AND a.status = :st " +
                "ORDER BY a.dataUpload DESC", Arquivo.class)).thenReturn(q);
        when(q.setParameter(eq("idp"), eq(5L))).thenReturn(q);
        when(q.setParameter(eq("st"), eq(IndicativoStatus.A))).thenReturn(q);
        when(q.getResultList()).thenReturn(Collections.emptyList());
        assertEquals(0, dao.listarPorPaciente(5L).size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void listarInativosParaGc_deveAplicarLimite() {
        TypedQuery<Arquivo> q = mock(TypedQuery.class);
        when(em.createQuery(
                "SELECT a FROM Arquivo a WHERE a.status = :st ORDER BY a.dataUpload ASC",
                Arquivo.class)).thenReturn(q);
        when(q.setParameter(eq("st"), eq(IndicativoStatus.I))).thenReturn(q);
        when(q.setMaxResults(50)).thenReturn(q);
        when(q.getResultList()).thenReturn(Collections.emptyList());
        assertEquals(0, dao.listarInativosParaGc(50).size());
        verify(q).setMaxResults(50);
    }
}
