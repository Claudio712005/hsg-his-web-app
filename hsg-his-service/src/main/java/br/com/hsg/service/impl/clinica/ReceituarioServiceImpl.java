package br.com.hsg.service.impl.clinica;

import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.dao.ReceitaDAO;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Receita;
import br.com.hsg.domain.entity.ReceitaItem;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.facade.clinica.ReceituarioServiceFacade;
import br.com.hsg.service.impl.notificacao.NotificacaoEmissor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ReceituarioServiceImpl implements ReceituarioServiceFacade {

    private static final Logger LOG = Logger.getLogger(ReceituarioServiceImpl.class.getName());

    @EJB private ReceitaDAO        receitaDAO;
    @EJB private ConsultaDAO       consultaDAO;
    @EJB private NotificacaoEmissor emissor;

    @Override
    public Receita emitir(Long idConsulta, Long idMedico, List<ItemDTO> itens) {
        if (idMedico == null) {
            throw new IllegalArgumentException("Médico é obrigatório.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("É necessário ao menos um item.");
        }
        Consulta c = requererConsulta(idConsulta);

        if (c.getMedico() == null || !c.getMedico().getId().equals(idMedico)) {
            throw new IllegalStateException("Apenas o médico responsável pela consulta pode emitir receita.");
        }
        if (c.getStatus() == StatusConsulta.CANCELADA || c.getStatus() == StatusConsulta.FALTOU) {
            throw new IllegalStateException("Consultas canceladas ou com falta não aceitam receita.");
        }

        int inativadas = receitaDAO.inativarAtivasPorConsulta(idConsulta);
        if (inativadas > 0) {
            LOG.log(Level.INFO, "[ReceituarioServiceImpl] {0} receita(s) inativada(s) para reemissão.",
                    inativadas);
        }

        List<ReceitaItem> items = new ArrayList<>();
        int ordem = 1;
        for (ItemDTO dto : itens) {
            if (dto == null) {
                throw new IllegalArgumentException("Item inválido.");
            }
            items.add(ReceitaItem.criar(dto.medicamento, dto.posologia, dto.observacao, dto.cid10, ordem++));
        }

        Receita nova = Receita.emitir(c, c.getMedico(), items);
        Receita salva = receitaDAO.salvar(nova);
        LOG.log(Level.INFO, "[ReceituarioServiceImpl] Receita emitida consulta={0} medico={1} itens={2}",
                new Object[]{idConsulta, idMedico, items.size()});

        boolean reemissao = inativadas > 0;
        notificar(c, reemissao ? "Receituário atualizado" : "Receituário emitido",
                reemissao
                    ? "O médico atualizou a receita da sua consulta. Baixe o PDF atualizado."
                    : "O médico emitiu uma receita para a sua consulta. Baixe o PDF no portal.");
        return salva;
    }

    @Override
    public void excluir(Long idConsulta, Long idMedico) {
        if (idMedico == null) {
            throw new IllegalArgumentException("Médico é obrigatório.");
        }
        Consulta c = requererConsulta(idConsulta);
        if (c.getMedico() == null || !c.getMedico().getId().equals(idMedico)) {
            throw new IllegalStateException("Apenas o médico responsável pode excluir o receituário.");
        }
        Receita ativa = receitaDAO.buscarAtivaPorConsulta(idConsulta);
        if (ativa == null) {
            throw new IllegalArgumentException("Não há receita ativa para excluir.");
        }
        ativa.inativar();
        receitaDAO.atualizar(ativa);
        LOG.log(Level.INFO, "[ReceituarioServiceImpl] Receita {0} excluída por médico {1}.",
                new Object[]{ativa.getId(), idMedico});

        notificar(c, "Receituário cancelado",
                "O médico cancelou a receita anterior da sua consulta. Aguarde nova orientação.");
    }

    private void notificar(Consulta c, String titulo, String mensagemPaciente) {
        if (emissor == null) return;
        try {
            if (c.getPaciente() != null) {
                emissor.emitir(TipoDestinatarioNotificacao.PACIENTE,
                        c.getPaciente().getId(), titulo, mensagemPaciente,
                        TipoNotificacao.INFO, CategoriaNotificacao.CONSULTA,
                        "/paciente/minhas-consultas.xhtml");
            }
            if (c.getMedico() != null) {
                emissor.emitir(TipoDestinatarioNotificacao.MEDICO,
                        c.getMedico().getId(), titulo,
                        "Ação registrada no receituário da sua consulta.",
                        TipoNotificacao.INFO, CategoriaNotificacao.CONSULTA,
                        "/clinica/minha-agenda.xhtml");
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "[ReceituarioServiceImpl] Falha ao notificar receita", ex);
        }
    }

    @Override
    public Receita buscarPorConsulta(Long idConsulta) {
        return receitaDAO.buscarAtivaPorConsulta(idConsulta);
    }

    @Override
    public Receita buscarParaPdf(Long idConsulta, Long idSolicitante, TipoResponsavel tipoSolicitante) {
        Receita r = receitaDAO.buscarAtivaPorConsulta(idConsulta);
        if (r == null) {
            throw new IllegalArgumentException("Receita não encontrada para essa consulta.");
        }
        autorizar(r, idSolicitante, tipoSolicitante);
        return r;
    }

    private void autorizar(Receita r, Long idSolic, TipoResponsavel tpSolic) {
        if (tpSolic == null) {
            throw new IllegalStateException("Sem permissão.");
        }
        switch (tpSolic) {
            case ADMIN:
            case ENFERMEIRO:
                return;
            case MEDICO:
                if (r.getConsulta() != null && r.getConsulta().getMedico() != null
                        && r.getConsulta().getMedico().getId().equals(idSolic)) return;
                throw new IllegalStateException("Médico não autorizado a baixar essa receita.");
            case PACIENTE:
                if (r.getConsulta() != null && r.getConsulta().getPaciente() != null
                        && r.getConsulta().getPaciente().getId().equals(idSolic)) return;
                throw new IllegalStateException("Paciente não autorizado a baixar essa receita.");
            default:
                throw new IllegalStateException("Tipo de solicitante não suportado.");
        }
    }

    private Consulta requererConsulta(Long idConsulta) {
        Consulta c = consultaDAO.buscarPorIdComMedico(idConsulta);
        if (c == null) {
            throw new IllegalArgumentException("Consulta não encontrada.");
        }
        return c;
    }
}
