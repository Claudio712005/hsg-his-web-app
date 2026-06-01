package br.com.hsg.service.impl.public_;

import br.com.hsg.dao.ContaUsuarioDAO;
import br.com.hsg.dao.EnfermeiroDAO;
import br.com.hsg.dao.EspecialidadeDAO;
import br.com.hsg.dao.MedicoDAO;
import br.com.hsg.dao.PreCadastroProfissionalDAO;
import br.com.hsg.domain.entity.ContaUsuario;
import br.com.hsg.domain.entity.Enfermeiro;
import br.com.hsg.domain.entity.Especialidade;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.PreCadastroProfissional;
import br.com.hsg.domain.vo.Coren;
import br.com.hsg.domain.vo.Crm;
import br.com.hsg.domain.vo.Email;
import br.com.hsg.domain.vo.NomeCompleto;
import br.com.hsg.domain.vo.Telefone;
import br.com.hsg.service.crypto.CpfCryptoService;
import br.com.hsg.service.dto.AtivacaoFormDTO;
import br.com.hsg.service.facade.public_.AtivacaoServiceFacade;
import br.com.hsg.service.keycloak.KeycloakAdminService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class AtivacaoServiceImpl implements AtivacaoServiceFacade {

    private static final Logger LOG = Logger.getLogger(AtivacaoServiceImpl.class.getName());

    @EJB private PreCadastroProfissionalDAO preCadastroDAO;
    @EJB private MedicoDAO                  medicoDAO;
    @EJB private EnfermeiroDAO              enfermeiroDAO;
    @EJB private EspecialidadeDAO           especialidadeDAO;
    @EJB private ContaUsuarioDAO            contaUsuarioDAO;
    @EJB private KeycloakAdminService       keycloakService;
    @EJB private CpfCryptoService           cpfCrypto;
    @EJB private br.com.hsg.service.impl.notificacao.NotificacaoEmissor emissor;

    @Override
    public PreCadastroProfissional validarToken(String token) {
        if (token == null || token.trim().isEmpty()) return null;
        return preCadastroDAO.buscarPorToken(token.trim());
    }

    @Override
    public void ativarCadastro(String token, AtivacaoFormDTO form) {

        validarFormulario(form);

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token de ativação inválido.");
        }
        PreCadastroProfissional pre = preCadastroDAO.buscarPorToken(token.trim());
        if (pre == null) {
            throw new IllegalArgumentException("Token não encontrado. Verifique o link recebido por e-mail.");
        }
        if (!pre.isPendente()) {
            throw new IllegalStateException("Este convite já foi utilizado. Seu cadastro já foi concluído.");
        }
        if (pre.isConviteExpirado()) {
            throw new IllegalStateException("Este convite expirou em "
                    + pre.getDataExpiracaoConviteFormatada()
                    + ". Entre em contato com a administração.");
        }

        String emailCorp = pre.getEmailCorporativo();
        String username  = emailCorp != null && emailCorp.contains("@")
                ? emailCorp.substring(0, emailCorp.indexOf('@'))
                : emailCorp;
        String[] nomeParts = dividirNome(pre.getNome());
        String roleName    = pre.isMedico() ? "hsg-medico" : "hsg-enfermeiro";

        String keycloakId = null;
        try {
            keycloakId = keycloakService.criarUsuario(username, emailCorp, nomeParts[0], nomeParts[1]);
            keycloakService.definirSenha(keycloakId, form.getSenha());
            keycloakService.atribuirRole(keycloakId, roleName);
            LOG.info("[AtivacaoServiceImpl] Usuário Keycloak criado e configurado: " + keycloakId);
        } catch (Exception e) {
            if (keycloakId != null) {
                keycloakService.tentarRemoverUsuario(keycloakId);
            }
            throw new RuntimeException(
                    "Erro ao criar conta no sistema de autenticação. Tente novamente mais tarde.", e);
        }

        try {
            String cpfDigits = pre.getCpf().replaceAll("\\D", "");
            String telDigits = form.getTelefone().replaceAll("\\D", "");
            LocalDate dataNasc = form.getDataNascimento()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            String cpfHash = cpfCrypto.hash(cpfDigits);
            String cpfEnc  = cpfCrypto.encrypt(cpfDigits);

            ContaUsuario conta = new ContaUsuario(null, keycloakId, username);
            contaUsuarioDAO.salvar(conta);

            NomeCompleto nome    = new NomeCompleto(nomeParts[0], nomeParts[1]);
            Email        email   = new Email(emailCorp);
            Telefone     telefone = new Telefone(telDigits);

            if (pre.isMedico()) {
                salvarMedico(pre, form, nome, email, telefone, dataNasc, cpfHash, cpfEnc, conta);
            } else {
                salvarEnfermeiro(pre, form, nome, email, telefone, dataNasc, cpfHash, cpfEnc, conta);
            }

            pre.concluir();
            preCadastroDAO.atualizar(pre);

            LOG.info("[AtivacaoServiceImpl] Cadastro concluído para: " + emailCorp);

            String tipoLabel = pre.isMedico() ? "Médico" : "Enfermeiro";
            if (emissor != null) emissor.emitirParaTodosAdmins(
                    "Profissional ativado",
                    tipoLabel + " " + pre.getNome() + " concluiu o cadastro e já pode acessar o sistema.",
                    br.com.hsg.domain.enums.TipoNotificacao.SUCESSO,
                    br.com.hsg.domain.enums.CategoriaNotificacao.SISTEMA,
                    "/admin/pre-cadastro-profissional.xhtml");

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "[AtivacaoServiceImpl] Falha ao persistir após criação KC. Compensando usuário: " + keycloakId, e);
            keycloakService.tentarRemoverUsuario(keycloakId);
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("Erro ao salvar os dados de cadastro. Tente novamente.", e);
        }
    }

    @Override
    public List<Especialidade> listarEspecialidadesAtivas() {
        return especialidadeDAO.listarAtivas();
    }

    private void salvarMedico(PreCadastroProfissional pre,
                               AtivacaoFormDTO form,
                               NomeCompleto nome, Email email, Telefone telefone,
                               LocalDate dataNasc,
                               String cpfHash, String cpfEnc,
                               ContaUsuario conta) {

        Especialidade esp = null;
        if (form.getEspecialidadeId() != null) {
            esp = especialidadeDAO.buscarPorId(form.getEspecialidadeId());
        } else if (pre.getEspecialidade() != null) {
            esp = especialidadeDAO.buscarPorNome(pre.getEspecialidade());
            if (esp == null) {
                LOG.severe("[AtivacaoServiceImpl] Especialidade não encontrada: '" + pre.getEspecialidade() + "'");
                throw new IllegalArgumentException(
                        "Especialidade \"" + pre.getEspecialidade() + "\" não encontrada. "
                        + "Contate a administração para corrigir o pré-cadastro.");
            }
        }
        if (esp == null) {
            throw new IllegalArgumentException("Especialidade é obrigatória para médicos.");
        }

        Medico m = Medico.criar(
                nome, email, telefone,
                new Crm(pre.getCrm(), pre.getUfCrm()),
                dataNasc, esp, conta);
        m.definirCpf(cpfHash, cpfEnc);
        medicoDAO.salvar(m);
    }

    private void salvarEnfermeiro(PreCadastroProfissional pre,
                                   AtivacaoFormDTO form,
                                   NomeCompleto nome, Email email, Telefone telefone,
                                   LocalDate dataNasc,
                                   String cpfHash, String cpfEnc,
                                   ContaUsuario conta) {

        Coren coren = new Coren(pre.getCoren(), pre.getUfCoren(), pre.getCategoriaCoren());
        Enfermeiro e = Enfermeiro.criar(
                nome, email, telefone, coren,
                dataNasc,
                form.getEspecialidadeEnf(),
                form.getSetor(),
                conta);
        e.definirCpf(cpfHash, cpfEnc);
        enfermeiroDAO.salvar(e);
    }

    private void validarFormulario(AtivacaoFormDTO form) {
        if (form == null) {
            throw new IllegalArgumentException("Formulário inválido.");
        }
        if (form.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória.");
        }
        LocalDate dataNasc = form.getDataNascimento()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (dataNasc.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("O profissional deve ter no mínimo 18 anos.");
        }
        if (dataNasc.isBefore(LocalDate.now().minusYears(100))) {
            throw new IllegalArgumentException("Data de nascimento inválida.");
        }

        String tel = form.getTelefone() != null ? form.getTelefone().replaceAll("\\D", "") : "";
        if (tel.length() != 11) {
            throw new IllegalArgumentException("Telefone inválido. Informe DDD + 9 dígitos (ex: 11912345678).");
        }

        String senha = form.getSenha();
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
        if (senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres.");
        }
        if (!senha.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("A senha deve conter ao menos uma letra maiúscula.");
        }
        if (!senha.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("A senha deve conter ao menos um número.");
        }
        if (!senha.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException("A senha deve conter ao menos um caractere especial (ex: @, #, !).");
        }
        if (!senha.equals(form.getConfirmacaoSenha())) {
            throw new IllegalArgumentException("A senha e a confirmação de senha não coincidem.");
        }
    }

    private String[] dividirNome(String nomeCompleto) {
        String nome = nomeCompleto == null ? "" : nomeCompleto.trim();
        int idx = nome.indexOf(' ');
        if (idx < 0) {
            return new String[]{nome, nome};
        }
        return new String[]{nome.substring(0, idx).trim(), nome.substring(idx + 1).trim()};
    }
}
