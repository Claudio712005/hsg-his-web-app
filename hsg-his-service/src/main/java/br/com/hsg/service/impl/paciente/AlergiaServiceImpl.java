package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.AlergiaDAO;
import br.com.hsg.dao.AlergiaHistoricoDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.domain.entity.Alergia;
import br.com.hsg.domain.entity.AlergiaHistorico;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.enums.AcaoAlergia;
import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import br.com.hsg.service.facade.paciente.AlergiaServiceFacade;
import br.com.hsg.service.impl.notificacao.NotificacaoEmissor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class AlergiaServiceImpl implements AlergiaServiceFacade {

    @EJB private AlergiaDAO          alergiaDAO;
    @EJB private AlergiaHistoricoDAO historicoDAO;
    @EJB private PacienteDAO         pacienteDAO;
    @EJB private NotificacaoEmissor  emissor;

    @Override
    public void informarAlergia(
            Long pacienteId,
            Long idCadastrador,
            String nome,
            String descricao,
            TipoAlergia tipoAlergia,
            GravidadeAlergia gravidadeAlergia,
            String reacao,
            LocalDateTime dataUltimaReacao,
            String observacao) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da alergia é obrigatório.");
        }
        if (tipoAlergia == null) {
            throw new IllegalArgumentException("O tipo da alergia é obrigatório.");
        }
        if (gravidadeAlergia == null) {
            throw new IllegalArgumentException("A gravidade da alergia é obrigatória.");
        }

        Paciente paciente = requererPaciente(pacienteId);

        Alergia alergia = Alergia.informar(
                paciente, idCadastrador,
                nome.trim(), descricao,
                tipoAlergia, gravidadeAlergia,
                reacao, dataUltimaReacao, observacao);

        alergiaDAO.salvar(alergia);
        historicoDAO.salvar(AlergiaHistorico.registrar(alergia, idCadastrador, AcaoAlergia.CRIADA));

        String pacienteNome = paciente.getNomeCompleto();
        if (emissor != null) emissor.emitir(TipoDestinatarioNotificacao.PACIENTE, paciente.getId(),
                "Nova alergia registrada",
                "A alergia '" + alergia.getNome() + "' foi registrada no seu prontuário e aguarda aprovação.",
                TipoNotificacao.INFO, CategoriaNotificacao.SISTEMA, null);
        if (emissor != null) emissor.emitirParaTodosAdmins(
                "Alergia aguardando aprovação",
                "Paciente " + pacienteNome + " registrou alergia '" + alergia.getNome()
                        + "' (gravidade " + alergia.getGravidadeAlergia() + ").",
                TipoNotificacao.ALERTA, CategoriaNotificacao.SISTEMA, null);
    }

    @Override
    public void excluirAlergia(Long alergiaId, Long idUsuario) {
        Alergia alergia = requererAlergia(alergiaId);
        alergia.validarExclusao();
        Long idPaciente = alergia.getPaciente() != null ? alergia.getPaciente().getId() : null;
        String nomeAlergia = alergia.getNome();
        historicoDAO.salvar(AlergiaHistorico.registrar(alergia, idUsuario, AcaoAlergia.EXCLUIDA));
        alergiaDAO.excluir(alergia);

        if (emissor != null) emissor.emitir(TipoDestinatarioNotificacao.PACIENTE, idPaciente,
                "Alergia removida",
                "A alergia '" + nomeAlergia + "' foi removida do seu prontuário.",
                TipoNotificacao.INFO, CategoriaNotificacao.SISTEMA, null);
    }

    @Override
    public List<Alergia> listarPorPaciente(Long pacienteId, int inicio, int tamanho) {
        return alergiaDAO.listarPorPaciente(pacienteId, inicio, tamanho);
    }

    @Override
    public long contarPorPaciente(Long pacienteId) {
        return alergiaDAO.contarPorPaciente(pacienteId);
    }

    @Override
    public Alergia buscarPorId(Long alergiaId) {
        return alergiaDAO.buscarPorId(alergiaId);
    }

    @Override
    public List<AlergiaHistorico> buscarHistorico(Long alergiaId) {
        return historicoDAO.listarPorAlergia(alergiaId);
    }

    private Paciente requererPaciente(Long id) {
        Paciente p = pacienteDAO.buscarPorId(id);
        if (p == null) throw new IllegalArgumentException("Paciente não encontrado.");
        return p;
    }

    @Override
    public void aprovarAlergia(Long alergiaId, Long idAprovador, String observacao) {
        Alergia alergia = requererAlergia(alergiaId);
        alergia.aprovar(idAprovador, observacao);
        alergiaDAO.atualizar(alergia);
        historicoDAO.salvar(AlergiaHistorico.registrar(alergia, idAprovador, AcaoAlergia.APROVADA));

        if (alergia.getPaciente() != null) {
            if (emissor != null) emissor.emitir(TipoDestinatarioNotificacao.PACIENTE, alergia.getPaciente().getId(),
                    "Alergia aprovada",
                    "A alergia '" + alergia.getNome() + "' foi validada pela equipe e consta oficialmente no seu prontuário.",
                    TipoNotificacao.SUCESSO, CategoriaNotificacao.SISTEMA, null);
        }
    }

    @Override
    public void rejeitarAlergia(Long alergiaId, Long idAprovador, String observacao) {
        Alergia alergia = requererAlergia(alergiaId);
        alergia.rejeitar(idAprovador, observacao);
        alergiaDAO.atualizar(alergia);
        historicoDAO.salvar(AlergiaHistorico.registrar(alergia, idAprovador, AcaoAlergia.REJEITADA));

        if (alergia.getPaciente() != null) {
            String motivoTxt = (observacao != null && !observacao.trim().isEmpty())
                    ? " Motivo: " + observacao : "";
            if (emissor != null) emissor.emitir(TipoDestinatarioNotificacao.PACIENTE, alergia.getPaciente().getId(),
                    "Alergia não validada",
                    "A alergia '" + alergia.getNome() + "' foi rejeitada pela equipe clínica." + motivoTxt,
                    TipoNotificacao.ALERTA, CategoriaNotificacao.SISTEMA, null);
        }
    }

    @Override
    public List<Alergia> listarParaAprovacao(int inicio, int tamanho) {
        return alergiaDAO.listarParaAprovacao(inicio, tamanho);
    }

    @Override
    public long contarParaAprovacao() {
        return alergiaDAO.contarParaAprovacao();
    }

    @Override
    public List<Alergia> listarParaAprovacao(int inicio, int tamanho,
                                             String filtroPaciente,
                                             TipoAlergia filtroTipo,
                                             GravidadeAlergia filtroGravidade) {
        return alergiaDAO.listarParaAprovacao(inicio, tamanho, filtroPaciente, filtroTipo, filtroGravidade);
    }

    @Override
    public long contarParaAprovacao(String filtroPaciente,
                                    TipoAlergia filtroTipo,
                                    GravidadeAlergia filtroGravidade) {
        return alergiaDAO.contarParaAprovacao(filtroPaciente, filtroTipo, filtroGravidade);
    }

    @Override
    public List<Alergia> listarParaAprovacao(int inicio, int tamanho,
                                             String filtroPaciente,
                                             TipoAlergia filtroTipo,
                                             GravidadeAlergia filtroGravidade,
                                             String sortField,
                                             boolean ascending) {
        return alergiaDAO.listarParaAprovacao(inicio, tamanho,
                filtroPaciente, filtroTipo, filtroGravidade, sortField, ascending);
    }

    @Override
    public List<Alergia> listarHistorico(int inicio, int tamanho,
                                         String filtroPaciente,
                                         TipoAlergia filtroTipo,
                                         GravidadeAlergia filtroGravidade,
                                         StatusAlergia filtroStatus,
                                         String sortField,
                                         boolean ascending) {
        return alergiaDAO.listarHistorico(inicio, tamanho,
                filtroPaciente, filtroTipo, filtroGravidade, filtroStatus, sortField, ascending);
    }

    @Override
    public long contarHistorico(String filtroPaciente,
                                TipoAlergia filtroTipo,
                                GravidadeAlergia filtroGravidade,
                                StatusAlergia filtroStatus) {
        return alergiaDAO.contarHistorico(filtroPaciente, filtroTipo, filtroGravidade, filtroStatus);
    }

    private Alergia requererAlergia(Long id) {
        Alergia a = alergiaDAO.buscarPorId(id);
        if (a == null) throw new IllegalArgumentException("Alergia não encontrada.");
        return a;
    }
}
