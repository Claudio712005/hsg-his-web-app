package br.com.hsg.service.impl.admin;

import br.com.hsg.dao.EnvioConviteHistoricoDAO;
import br.com.hsg.dao.PreCadastroProfissionalDAO;
import br.com.hsg.domain.entity.EnvioConviteHistorico;
import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.domain.enums.CategoriaCoren;
import br.com.hsg.domain.enums.StatusPreCadastro;
import br.com.hsg.domain.enums.TipoProfissional;
import br.com.hsg.service.email.EmailCorporativoService;
import br.com.hsg.service.facade.admin.PreCadastroServiceFacade;
import br.com.hsg.service.mail.MailService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class PreCadastroServiceImpl implements PreCadastroServiceFacade {

    private static final int DIAS_EXPIRACAO_CONVITE = 2;

    @EJB private PreCadastroProfissionalDAO preCadastroDAO;
    @EJB private EnvioConviteHistoricoDAO   historicoDAO;
    @EJB private MailService                mailService;
    @EJB private EmailCorporativoService    emailCorporativoService;

    @Override
    public PreCadastroProfissional criarParaMedico(
            String nome, String emailPessoal, String cpf,
            String crm, String ufCrm, String especialidade, Long idAdminCriador) {

        validarCamposComuns(nome, emailPessoal, cpf, idAdminCriador);
        validarCamposMedico(crm, ufCrm);
        validarDuplicidade(emailPessoal, cpf);

        PreCadastroProfissional p = PreCadastroProfissional.criarParaMedico(
                nome.trim(), emailPessoal.trim().toLowerCase(), cpf.trim(),
                crm.trim(), ufCrm.trim().toUpperCase(), especialidade,
                idAdminCriador);

        p = preCadastroDAO.salvar(p);

        p.definirEmailCorporativo(emailCorporativoService.gerar(nome));
        return preCadastroDAO.atualizar(p);
    }

    @Override
    public PreCadastroProfissional criarParaEnfermeiro(
            String nome, String emailPessoal, String cpf,
            String coren, String ufCoren, CategoriaCoren categoriaCoren, Long idAdminCriador) {

        validarCamposComuns(nome, emailPessoal, cpf, idAdminCriador);
        validarCamposEnfermeiro(coren, ufCoren, categoriaCoren);
        validarDuplicidade(emailPessoal, cpf);

        PreCadastroProfissional p = PreCadastroProfissional.criarParaEnfermeiro(
                nome.trim(), emailPessoal.trim().toLowerCase(), cpf.trim(),
                coren.trim(), ufCoren.trim().toUpperCase(), categoriaCoren,
                idAdminCriador);

        p = preCadastroDAO.salvar(p);

        p.definirEmailCorporativo(emailCorporativoService.gerar(nome));
        return preCadastroDAO.atualizar(p);
    }

    @Override
    public void enviarConvite(Long preCadastroId, Long idAdmin, String nomeAdmin) {
        PreCadastroProfissional preCadastro = requerer(preCadastroId);

        if (!preCadastro.isPendente()) {
            throw new IllegalStateException("Convite só pode ser enviado para pré-cadastros com status PENDENTE.");
        }

        if (preCadastro.isEmailEnviado() && !preCadastro.isConviteExpirado()) {
            throw new IllegalStateException("O convite anterior ainda está válido. Aguarde sua expiração antes de reenviar.");
        }

        preCadastro.registrarEnvioEmail(DIAS_EXPIRACAO_CONVITE);
        preCadastroDAO.atualizar(preCadastro);

        LocalDateTime dataExpiracao = preCadastro.getDataExpiracaoConvite();

        try {
            mailService.enviarConviteProfissional(
                    preCadastro.getNome(),
                    preCadastro.getEmailPessoal(),
                    preCadastro.getTipoProfissional(),
                    preCadastro.getTokenConvite(),
                    preCadastro.getEmailCorporativo());

            historicoDAO.salvar(EnvioConviteHistorico.registrarEnvio(
                    preCadastro, idAdmin, nomeAdmin, dataExpiracao));

        } catch (RuntimeException e) {
            historicoDAO.salvar(EnvioConviteHistorico.registrarErro(
                    preCadastro, idAdmin, nomeAdmin, dataExpiracao,
                    e.getMessage() != null ? e.getMessage() : "Falha desconhecida no envio do e-mail."));
            throw e;
        }
    }

    @Override
    public void concluirCadastro(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token inválido.");
        }
        PreCadastroProfissional p = preCadastroDAO.buscarPorToken(token.trim());
        if (p == null) {
            throw new IllegalArgumentException("Token inválido ou não encontrado.");
        }
        if (!p.isPendente()) {
            throw new IllegalStateException("Este convite já foi utilizado ou o cadastro já foi concluído.");
        }
        if (p.isConviteExpirado()) {
            throw new IllegalStateException("Este convite expirou. Entre em contato com a administração do sistema.");
        }
        p.concluir();
        preCadastroDAO.atualizar(p);
    }

    @Override
    public List<EnvioConviteHistorico> buscarHistorico(Long preCadastroId) {
        return historicoDAO.listarPorPreCadastro(preCadastroId);
    }

    @Override
    public PreCadastroProfissional buscarPorToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token inválido.");
        }
        return preCadastroDAO.buscarPorToken(token.trim());
    }

    @Override
    public PreCadastroProfissional buscarPorId(Long id) {
        return preCadastroDAO.buscarPorId(id);
    }

    @Override
    public List<PreCadastroProfissional> listarTodos(int inicio, int tamanho) {
        return preCadastroDAO.listarTodos(inicio, tamanho);
    }

    @Override
    public List<PreCadastroProfissional> listarPorStatus(StatusPreCadastro status, int inicio, int tamanho) {
        return preCadastroDAO.listarPorStatus(status, inicio, tamanho);
    }

    @Override
    public List<PreCadastroProfissional> listarPorTipo(TipoProfissional tipo, int inicio, int tamanho) {
        return preCadastroDAO.listarPorTipo(tipo, inicio, tamanho);
    }

    @Override
    public long contar() {
        return preCadastroDAO.contar();
    }

    @Override
    public long contarPorStatus(StatusPreCadastro status) {
        return preCadastroDAO.contarPorStatus(status);
    }

    private void validarCamposComuns(String nome, String emailPessoal, String cpf, Long idAdmin) {
        if (nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("Nome é obrigatório.");
        if (emailPessoal == null || !emailPessoal.contains("@"))
            throw new IllegalArgumentException("E-mail pessoal inválido.");
        if (cpf == null || cpf.trim().isEmpty())
            throw new IllegalArgumentException("CPF é obrigatório.");
        if (idAdmin == null)
            throw new IllegalArgumentException("Administrador criador é obrigatório.");
    }

    private void validarCamposMedico(String crm, String ufCrm) {
        if (crm == null || crm.trim().isEmpty())
            throw new IllegalArgumentException("CRM é obrigatório para médicos.");
        if (ufCrm == null || ufCrm.trim().isEmpty())
            throw new IllegalArgumentException("UF do CRM é obrigatória para médicos.");
    }

    private void validarCamposEnfermeiro(String coren, String ufCoren, CategoriaCoren categoria) {
        if (coren == null || coren.trim().isEmpty())
            throw new IllegalArgumentException("COREN é obrigatório para enfermeiros.");
        if (ufCoren == null || ufCoren.trim().isEmpty())
            throw new IllegalArgumentException("UF do COREN é obrigatória para enfermeiros.");
        if (categoria == null)
            throw new IllegalArgumentException("Categoria do COREN é obrigatória para enfermeiros.");
    }

    private void validarDuplicidade(String emailPessoal, String cpf) {
        if (preCadastroDAO.existeEmailPendente(emailPessoal.trim().toLowerCase()))
            throw new IllegalStateException("Já existe um pré-cadastro pendente para este e-mail pessoal.");
        if (preCadastroDAO.existeCpfPendente(cpf.trim()))
            throw new IllegalStateException("Já existe um pré-cadastro pendente para este CPF.");
    }

    private PreCadastroProfissional requerer(Long id) {
        PreCadastroProfissional p = preCadastroDAO.buscarPorId(id);
        if (p == null) throw new IllegalArgumentException("Pré-cadastro não encontrado.");
        return p;
    }
}
