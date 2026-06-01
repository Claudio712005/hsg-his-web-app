package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.ConvenioDAO;
import br.com.hsg.dao.PacienteConvenioDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.dao.PlanoConvenioDAO;
import br.com.hsg.dao.RegraCoberturaDAO;
import br.com.hsg.dao.SolicitacaoConvenioDAO;
import br.com.hsg.domain.entity.Convenio;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.PacienteConvenio;
import br.com.hsg.domain.entity.PlanoConvenio;
import br.com.hsg.domain.entity.RegraCobertura;
import br.com.hsg.domain.entity.SolicitacaoConvenio;
import br.com.hsg.domain.enums.TipoTitularidade;
import br.com.hsg.dao.AdminDAO;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import br.com.hsg.service.crypto.CarteirinhaCryptoService;
import br.com.hsg.service.facade.notificacao.NotificacaoServiceFacade;
import br.com.hsg.service.facade.paciente.ConvenioPacienteServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class ConvenioPacienteServiceImpl implements ConvenioPacienteServiceFacade {

    private static final Logger LOG = Logger.getLogger(ConvenioPacienteServiceImpl.class.getName());

    @EJB private PacienteConvenioDAO    pacienteConvenioDAO;
    @EJB private SolicitacaoConvenioDAO solicitacaoConvenioDAO;
    @EJB private PacienteDAO            pacienteDAO;
    @EJB private PlanoConvenioDAO       planoConvenioDAO;
    @EJB private ConvenioDAO            convenioDAO;
    @EJB private RegraCoberturaDAO      regraCoberturaDAO;
    @EJB private CarteirinhaCryptoService carteirinhaCrypto;
    @EJB private AdminDAO                 adminDAO;
    @EJB private NotificacaoServiceFacade notificacaoService;

    @Override
    public PacienteConvenio buscarConvenioAtivo(Long idPaciente) {
        return pacienteConvenioDAO.buscarAtivoPorPaciente(idPaciente);
    }

    @Override
    public List<PacienteConvenio> listarHistorico(Long idPaciente) {
        return pacienteConvenioDAO.listarHistoricoPorPaciente(idPaciente);
    }

    @Override
    public List<SolicitacaoConvenio> listarSolicitacoes(Long idPaciente) {
        return solicitacaoConvenioDAO.listarPorPaciente(idPaciente);
    }

    @Override
    public boolean possuiSolicitacaoPendente(Long idPaciente) {
        return solicitacaoConvenioDAO.existePendentePorPaciente(idPaciente);
    }

    @Override
    public List<Convenio> listarConveniosAtivos() {
        return convenioDAO.listarAtivos();
    }

    @Override
    public List<PlanoConvenio> listarPlanosAtivosPorConvenio(Long idConvenio) {
        return planoConvenioDAO.listarAtivosPorConvenio(idConvenio);
    }

    @Override
    public PlanoConvenio buscarPlano(Long idPlano) {
        return idPlano != null ? planoConvenioDAO.buscarPorId(idPlano) : null;
    }

    @Override
    public List<RegraCobertura> listarRegrasDoPlano(Long idPlano) {
        return idPlano != null ? regraCoberturaDAO.listarAtivasPorPlano(idPlano)
                : java.util.Collections.emptyList();
    }

    @Override
    public void solicitarConvenio(Long idPaciente, Long idPlano, String numeroCarteirinha,
                                  LocalDate dataValidade, TipoTitularidade tipoTitularidade, String motivo) {
        Paciente paciente = pacienteDAO.buscarPorId(idPaciente);
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente não encontrado.");
        }
        PlanoConvenio plano = planoConvenioDAO.buscarPorId(idPlano);
        if (plano == null) {
            throw new IllegalArgumentException("Plano não encontrado.");
        }
        if (!plano.isAtivo()) {
            throw new IllegalStateException("O plano selecionado não está disponível.");
        }
        if (numeroCarteirinha == null || numeroCarteirinha.trim().isEmpty()) {
            throw new IllegalArgumentException("O número da carteirinha é obrigatório.");
        }
        if (solicitacaoConvenioDAO.existePendentePorPaciente(idPaciente)) {
            throw new IllegalStateException("Você já possui uma solicitação de convênio pendente de aprovação.");
        }

        String numeroLimpo = numeroCarteirinha.trim();
        String enc     = carteirinhaCrypto.encrypt(numeroLimpo);
        String mascara = carteirinhaCrypto.mascarar(numeroLimpo);

        String snapshotPlanoAtual = null;
        PacienteConvenio ativo = pacienteConvenioDAO.buscarAtivoPorPaciente(idPaciente);
        if (ativo != null) {
            snapshotPlanoAtual = ativo.getPlano().getConvenio().getNome() + " — " + ativo.getPlano().getNome();
        }

        SolicitacaoConvenio s = SolicitacaoConvenio.solicitar(
                paciente, plano, enc, mascara, dataValidade, tipoTitularidade, motivo, snapshotPlanoAtual);
        solicitacaoConvenioDAO.salvar(s);
        LOG.info("[ConvenioPacienteServiceImpl] Solicitação de convênio criada: paciente=" + idPaciente
                + ", plano=" + idPlano);

        try {
            String tituloAdmin = "Nova solicitação de convênio";
            String msgAdmin = "Paciente " + paciente.getNomeCompleto() + " solicitou adesão ao plano "
                    + plano.getConvenio().getNome() + " — " + plano.getNome() + ".";
            for (Long idAdmin : adminDAO.listarIdsAtivos()) {
                notificacaoService.notificar(TipoDestinatarioNotificacao.ADMIN, idAdmin,
                        tituloAdmin, msgAdmin, TipoNotificacao.INFO,
                        CategoriaNotificacao.CONVENIO, "/admin/aprovacao-convenios.xhtml");
            }
        } catch (Exception ex) {
            LOG.log(java.util.logging.Level.WARNING,
                    "[ConvenioPacienteServiceImpl] Falha ao notificar admins", ex);
        }
    }

    @Override
    public void cancelarSolicitacao(Long idSolicitacao, Long idPaciente) {
        SolicitacaoConvenio s = solicitacaoConvenioDAO.buscarPorId(idSolicitacao);
        if (s == null) {
            throw new IllegalArgumentException("Solicitação não encontrada.");
        }
        if (!s.getPaciente().getId().equals(idPaciente)) {
            throw new IllegalStateException("Esta solicitação não pertence ao paciente.");
        }
        s.cancelar();
        solicitacaoConvenioDAO.atualizar(s);
        LOG.info("[ConvenioPacienteServiceImpl] Solicitação cancelada: id=" + idSolicitacao);

        try {
            String pacienteNome = s.getPaciente().getNomeCompleto();
            String plano = s.getPlano().getConvenio().getNome() + " — " + s.getPlano().getNome();
            String msg = "Paciente " + pacienteNome + " cancelou a solicitação de adesão ao plano "
                    + plano + ".";
            for (Long idAdmin : adminDAO.listarIdsAtivos()) {
                notificacaoService.notificar(TipoDestinatarioNotificacao.ADMIN, idAdmin,
                        "Solicitação de convênio cancelada", msg, TipoNotificacao.INFO,
                        CategoriaNotificacao.CONVENIO, "/admin/aprovacao-convenios.xhtml");
            }
        } catch (Exception ex) {
            LOG.log(java.util.logging.Level.WARNING,
                    "[ConvenioPacienteServiceImpl] Falha ao notificar admins do cancelamento", ex);
        }
    }
}
