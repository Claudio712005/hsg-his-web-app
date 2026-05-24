package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.PacienteConvenioDAO;
import br.com.hsg.dao.RegraCoberturaDAO;
import br.com.hsg.dao.SolicitacaoConvenioDAO;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.service.crypto.CarteirinhaCryptoService;
import br.com.hsg.service.facade.admin.AprovacaoConvenioServiceFacade;
import br.com.hsg.service.mail.MailService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class AprovacaoConvenioServiceImpl implements AprovacaoConvenioServiceFacade {

    private static final Logger LOG = Logger.getLogger(AprovacaoConvenioServiceImpl.class.getName());
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @EJB private SolicitacaoConvenioDAO solicitacaoConvenioDAO;
    @EJB private PacienteConvenioDAO    pacienteConvenioDAO;
    @EJB private RegraCoberturaDAO      regraCoberturaDAO;
    @EJB private CarteirinhaCryptoService carteirinhaCrypto;
    @EJB private MailService            mailService;

    @Override
    public SolicitacaoConvenio buscarPorId(Long id) {
        return solicitacaoConvenioDAO.buscarPorId(id);
    }

    @Override
    public List<RegraCobertura> listarRegrasDoPlano(Long idPlano) {
        return idPlano != null ? regraCoberturaDAO.listarAtivasPorPlano(idPlano)
                : Collections.emptyList();
    }

    @Override
    public List<SolicitacaoConvenio> listarPaginado(int primeiro, int tamanho,
                                                    String filtroPaciente, String filtroStatus,
                                                    String campoOrdenacao, boolean crescente) {
        return solicitacaoConvenioDAO.listarPaginado(primeiro, tamanho, filtroPaciente, filtroStatus,
                campoOrdenacao, crescente);
    }

    @Override
    public long contarTotal(String filtroPaciente, String filtroStatus) {
        return solicitacaoConvenioDAO.contarTotal(filtroPaciente, filtroStatus);
    }

    @Override
    public long contarPendentes() {
        return solicitacaoConvenioDAO.contarPendentes();
    }

    @Override
    public void aprovar(Long idSolicitacao, Long idAprovador) {
        SolicitacaoConvenio s = solicitacaoConvenioDAO.buscarPorId(idSolicitacao);
        if (s == null) {
            throw new IllegalArgumentException("Solicitação não encontrada.");
        }

        PacienteConvenio ativo = pacienteConvenioDAO.buscarAtivoPorPaciente(s.getPaciente().getId());
        if (ativo != null) {
            ativo.cancelar();
            pacienteConvenioDAO.atualizar(ativo);
        }

        String numero  = carteirinhaCrypto.decrypt(s.getCarteirinhaEnc());
        String hash    = carteirinhaCrypto.hash(numero);

        PacienteConvenio novo = PacienteConvenio.criar(
                s.getPaciente(), s.getPlano(), hash, s.getCarteirinhaEnc(),
                s.getCarteirinhaMascara(), s.getDataValidade(), s.getTipoTitularidade(), idAprovador);
        pacienteConvenioDAO.salvar(novo);

        s.aprovar(idAprovador);
        solicitacaoConvenioDAO.atualizar(s);
        LOG.info("[AprovacaoConvenioServiceImpl] Solicitação aprovada: id=" + idSolicitacao
                + ", paciente=" + s.getPaciente().getId());

        notificarAprovacao(s);
    }

    @Override
    public void rejeitar(Long idSolicitacao, Long idAprovador, String motivoRejeicao) {
        SolicitacaoConvenio s = solicitacaoConvenioDAO.buscarPorId(idSolicitacao);
        if (s == null) {
            throw new IllegalArgumentException("Solicitação não encontrada.");
        }
        s.rejeitar(idAprovador, motivoRejeicao);
        solicitacaoConvenioDAO.atualizar(s);
        LOG.info("[AprovacaoConvenioServiceImpl] Solicitação rejeitada: id=" + idSolicitacao);

        notificarRejeicao(s);
    }

    private void notificarAprovacao(SolicitacaoConvenio s) {
        try {
            String email = s.getPaciente().getEmail();
            if (email == null || email.trim().isEmpty()) {
                LOG.warning("[AprovacaoConvenioServiceImpl] Paciente sem e-mail; notificação de aprovação não enviada.");
                return;
            }
            LocalDate validade = s.getDataValidade();
            mailService.enviarConvenioAprovado(
                    s.getPaciente().getNomeCompleto(),
                    email,
                    s.getPlano().getConvenio().getNome(),
                    s.getPlano().getNome(),
                    s.getCarteirinhaMascara(),
                    validade != null ? validade.format(FMT_DATA) : null);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "[AprovacaoConvenioServiceImpl] Falha ao enviar e-mail de aprovação (aprovação mantida).", e);
        }
    }

    private void notificarRejeicao(SolicitacaoConvenio s) {
        try {
            String email = s.getPaciente().getEmail();
            if (email == null || email.trim().isEmpty()) {
                LOG.warning("[AprovacaoConvenioServiceImpl] Paciente sem e-mail; notificação de rejeição não enviada.");
                return;
            }
            mailService.enviarConvenioRejeitado(
                    s.getPaciente().getNomeCompleto(),
                    email,
                    s.getPlano().getConvenio().getNome(),
                    s.getPlano().getNome(),
                    s.getMotivoRejeicao());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "[AprovacaoConvenioServiceImpl] Falha ao enviar e-mail de rejeição (rejeição mantida).", e);
        }
    }
}
