package br.com.hsg.service.impl.paciente;

import br.com.hsg.dao.EnderecoDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.dao.PainelPacienteDAO;
import br.com.hsg.dao.SolicitacaoAtualizacaoDAO;
import br.com.hsg.domain.entity.Endereco;
import br.com.hsg.domain.entity.MedicaoPaciente;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.SolicitacaoAtualizacao;
import br.com.hsg.domain.entity.TipoSanguineo;
import br.com.hsg.domain.enums.Estado;
import br.com.hsg.domain.enums.TipoCancelador;
import br.com.hsg.domain.enums.TipoSolicitacao;
import br.com.hsg.domain.vo.Email;
import br.com.hsg.domain.vo.NomeCompleto;
import br.com.hsg.domain.vo.Telefone;
import br.com.hsg.service.facade.paciente.SolicitacaoAtualizacaoServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class SolicitacaoAtualizacaoServiceImpl implements SolicitacaoAtualizacaoServiceFacade {

    @EJB private SolicitacaoAtualizacaoDAO solicitacaoDAO;
    @EJB private PacienteDAO               pacienteDAO;
    @EJB private EnderecoDAO               enderecoDAO;
    @EJB private PainelPacienteDAO         painelDAO;

    @Override
    public void solicitarAtualizacaoCadastral(
            Long pacienteId,
            String primeiroNome, String sobrenome,
            String email, String telefone,
            String motivo
    )throws IllegalArgumentException {
        Paciente paciente = requererPaciente(pacienteId);

        NomeCompleto novoNome  = new NomeCompleto(primeiroNome.trim(), sobrenome.trim());
        Email        novoEmail = new Email(email.trim());
        Telefone     novoTel   = null;
        if (telefone != null && !telefone.trim().isEmpty()) {
            novoTel = new Telefone(telefone.replaceAll("\\D", ""));
        }

        solicitacaoDAO.salvar(
                SolicitacaoAtualizacao.solicitarCadastral(paciente, novoNome, novoEmail, novoTel, motivo)
        );
    }

    @Override
    public void solicitarAtualizacaoEndereco(
            Long pacienteId,
            String logradouro, String numero, String complemento,
            String bairro, String cidade, Estado estado, String cep,
            String motivo
    ) throws IllegalArgumentException {
        Paciente paciente     = requererPaciente(pacienteId);
        Endereco enderecoAtual = enderecoDAO.buscarPorPaciente(pacienteId);

        solicitacaoDAO.salvar(
                SolicitacaoAtualizacao.solicitarEndereco(
                        paciente, enderecoAtual,
                        logradouro, numero, complemento, bairro, cidade, estado, cep,
                        motivo
                )
        );
    }

    @Override
    public void solicitarAtualizacaoClinica(
            Long pacienteId,
            Double peso, Double altura,
            String tipoSanguineo,
            String motivo
    )throws IllegalArgumentException {
        Paciente        paciente  = requererPaciente(pacienteId);
        MedicaoPaciente medicao   = painelDAO.buscarUltimaMedicao(pacienteId);
        TipoSanguineo   tipoAtual = painelDAO.buscarUltimoTipoSanguineo(pacienteId);

        Double snpPeso   = medicao   != null ? medicao.getPeso()   : null;
        Double snpAltura = medicao   != null ? medicao.getAltura() : null;
        String snpTpSang = tipoAtual != null && tipoAtual.getTipoSanguineo() != null
                ? tipoAtual.getTipoSanguineo().name() : null;

        solicitacaoDAO.salvar(
                SolicitacaoAtualizacao.solicitarClinica(
                        paciente,
                        snpPeso, snpAltura, snpTpSang,
                        peso, altura, tipoSanguineo,
                        motivo
                )
        );
    }

    @Override
    public void cancelarSolicitacao(Long solicitacaoId, Long idCancelador,
            TipoCancelador tipoCancelador, String motivoCancelamento) {
        SolicitacaoAtualizacao s = solicitacaoDAO.buscarPorId(solicitacaoId);
        if (s == null) {
            throw new IllegalArgumentException("Solicitação não encontrada.");
        }
        s.cancelar(idCancelador, tipoCancelador, motivoCancelamento);
        solicitacaoDAO.atualizar(s);
    }

    @Override
    public List<SolicitacaoAtualizacao> consultarHistorico(
            Long pacienteId, int inicio, int tamanho, List<TipoSolicitacao> tipos) {
        return solicitacaoDAO.buscarPorPaciente(pacienteId, inicio, tamanho, tipos);
    }

    @Override
    public long contarHistorico(Long pacienteId, List<TipoSolicitacao> tipos) {
        return solicitacaoDAO.contarPorPaciente(pacienteId, tipos);
    }

    @Override
    public Endereco buscarEnderecoPorPaciente(Long pacienteId) {
        return enderecoDAO.buscarPorPaciente(pacienteId);
    }

    private Paciente requererPaciente(Long id) {
        Paciente p = pacienteDAO.buscarPorId(id);
        if (p == null) throw new IllegalArgumentException("Paciente não encontrado.");
        return p;
    }
}
