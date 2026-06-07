package br.com.hsg.service.impl.storage;

import br.com.hsg.dao.ArquivoDAO;
import br.com.hsg.dao.ConsultaAnotacaoDAO;
import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.domain.entity.Arquivo;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.ConsultaAnotacao;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.enums.StatusConsulta;
import br.com.hsg.domain.enums.StorageDomain;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.facade.storage.ArquivoServiceFacade;
import br.com.hsg.service.facade.storage.ObjectStorageService;
import br.com.hsg.service.facade.storage.StoragePathResolver;
import br.com.hsg.service.facade.storage.StoragePutResult;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ArquivoServiceImpl implements ArquivoServiceFacade {

    private static final Logger LOG = Logger.getLogger(ArquivoServiceImpl.class.getName());

    static final String ENV_MAX_BYTES   = "APP_STORAGE_MAX_BYTES";
    static final String ENV_PRESIGN_TTL = "APP_STORAGE_PRESIGN_TTL_MIN";

    private static final Set<TipoResponsavel> ANEXO_CLINICO = EnumSet.of(
            TipoResponsavel.MEDICO, TipoResponsavel.ENFERMEIRO, TipoResponsavel.ADMIN);

    @EJB private ArquivoDAO            arquivoDAO;
    @EJB private ConsultaDAO           consultaDAO;
    @EJB private ConsultaAnotacaoDAO   anotacaoDAO;
    @EJB private StoragePathResolver   pathResolver;
    @EJB private ObjectStorageService  storage;

    @Override
    public Arquivo anexarEmConsulta(Long idConsulta, byte[] payload, String contentType,
                                      String filename, Long idResponsavel,
                                      TipoResponsavel tipoResponsavel) {
        validarTipoClinico(tipoResponsavel);
        Consulta c = requererConsulta(idConsulta);
        bloquearSeCancelada(c);
        if (tipoResponsavel == TipoResponsavel.MEDICO) {
            if (c.getMedico() == null || !c.getMedico().getId().equals(idResponsavel)) {
                throw new IllegalStateException("Médicos só podem anexar nas próprias consultas.");
            }
        }
        return persistir(StorageDomain.ANEXO_CONSULTA, idConsulta, payload, contentType, filename,
                idResponsavel, tipoResponsavel);
    }

    @Override
    public Arquivo anexarExameEmConsulta(Long idConsulta, byte[] payload, String contentType,
                                            String filename, Long idResponsavel,
                                            TipoResponsavel tipoResponsavel) {
        if (tipoResponsavel == null) {
            throw new IllegalArgumentException("O tipo do responsável é obrigatório.");
        }
        Consulta c = requererConsulta(idConsulta);
        bloquearSeCancelada(c);
        switch (tipoResponsavel) {
            case PACIENTE:
                if (c.getPaciente() == null || !c.getPaciente().getId().equals(idResponsavel)) {
                    throw new IllegalStateException("Pacientes só podem anexar exames nas próprias consultas.");
                }
                if (c.getStatus() != StatusConsulta.AGENDADA
                        && c.getStatus() != StatusConsulta.CONFIRMADA) {
                    throw new IllegalStateException(
                            "Exames só podem ser anexados antes do atendimento (AGENDADA/CONFIRMADA).");
                }
                break;
            case MEDICO:
                if (c.getMedico() == null || !c.getMedico().getId().equals(idResponsavel)) {
                    throw new IllegalStateException("Médicos só podem anexar exames nas próprias consultas.");
                }
                break;
            case ENFERMEIRO:
            case ADMIN:
                break;
            default:
                throw new IllegalStateException("Tipo de responsável inválido para anexar exame.");
        }
        return persistir(StorageDomain.EXAME_CONSULTA, idConsulta, payload, contentType, filename,
                idResponsavel, tipoResponsavel);
    }

    @Override
    public Arquivo anexarEmAnotacao(Long idAnotacao, byte[] payload, String contentType,
                                      String filename, Long idResponsavel,
                                      TipoResponsavel tipoResponsavel) {
        validarTipoClinico(tipoResponsavel);
        ConsultaAnotacao a = requererAnotacao(idAnotacao);
        Consulta c = a.getConsulta();
        bloquearSeCancelada(c);
        if (tipoResponsavel == TipoResponsavel.MEDICO) {
            if (c.getMedico() == null || !c.getMedico().getId().equals(idResponsavel)) {
                throw new IllegalStateException("Médicos só podem anexar nas próprias consultas.");
            }
        }
        return persistir(StorageDomain.ANEXO_ANOTACAO, idAnotacao, payload, contentType, filename,
                idResponsavel, tipoResponsavel);
    }

    @Override
    public List<Arquivo> listarPorConsulta(Long idConsulta) {
        if (idConsulta == null) return Collections.emptyList();
        return arquivoDAO.listarPorConsulta(idConsulta);
    }

    @Override
    public List<Arquivo> listarPorAnotacao(Long idAnotacao) {
        if (idAnotacao == null) return Collections.emptyList();
        return arquivoDAO.listarPorAnotacao(idAnotacao);
    }

    @Override
    public byte[] download(Long idArquivo, Long idSolicitante, TipoResponsavel tipoSolicitante) {
        Arquivo a = metadados(idArquivo, idSolicitante, tipoSolicitante);
        return storage.get(a.getPathLogico());
    }

    @Override
    public Arquivo metadados(Long idArquivo, Long idSolicitante, TipoResponsavel tipoSolicitante) {
        Arquivo a = arquivoDAO.buscarPorId(idArquivo);
        if (a == null || a.getStatus() != IndicativoStatus.A) {
            throw new IllegalArgumentException("Arquivo não encontrado.");
        }
        autorizarLeitura(a, idSolicitante, tipoSolicitante);
        return a;
    }

    @Override
    public URL urlDownload(Long idArquivo, Long idSolicitante, TipoResponsavel tipoSolicitante) {
        Arquivo a = arquivoDAO.buscarPorId(idArquivo);
        if (a == null || a.getStatus() != IndicativoStatus.A) {
            throw new IllegalArgumentException("Arquivo não encontrado.");
        }
        autorizarLeitura(a, idSolicitante, tipoSolicitante);
        long ttlMin = parseLongEnv(ENV_PRESIGN_TTL, 15L);
        return storage.presignedGet(a.getPathLogico(), Duration.ofMinutes(ttlMin));
    }

    @Override
    public void remover(Long idArquivo, Long idSolicitante, TipoResponsavel tipoSolicitante) {
        Arquivo a = arquivoDAO.buscarPorId(idArquivo);
        if (a == null || a.getStatus() != IndicativoStatus.A) {
            throw new IllegalArgumentException("Arquivo não encontrado.");
        }
        boolean autorOriginal = a.getIdResponsavel().equals(idSolicitante)
                && a.getTipoResponsavel() == tipoSolicitante;
        boolean admin = tipoSolicitante == TipoResponsavel.ADMIN;
        if (!autorOriginal && !admin) {
            throw new IllegalStateException("Sem permissão para remover este arquivo.");
        }
        a.inativar();
        arquivoDAO.atualizar(a);
        LOG.log(Level.INFO, "[ArquivoServiceImpl] Arquivo {0} inativado por {1}/{2}",
                new Object[]{idArquivo, idSolicitante, tipoSolicitante});
    }

    private Arquivo persistir(StorageDomain dominio, long ownerId, byte[] payload, String contentType,
                                String filename, Long idResponsavel, TipoResponsavel tipoResponsavel) {
        if (payload == null || payload.length == 0) {
            throw new IllegalArgumentException("O arquivo está vazio.");
        }
        long maxBytes = parseLongEnv(ENV_MAX_BYTES, StorageGuard.DEFAULT_MAX_BYTES);
        byte[] head = Arrays.copyOf(payload, Math.min(16, payload.length));
        StorageGuard.validar(filename, contentType, payload.length, maxBytes, head);
        String nomeSanitizado = StorageGuard.sanitizeFilename(filename);
        String pathLogico = pathResolver.buildLogicalPath(dominio, ownerId, nomeSanitizado);
        StoragePutResult posto = storage.put(pathLogico,
                new ByteArrayInputStream(payload), payload.length, contentType);
        String sha = sha256Hex(payload);
        Arquivo a = Arquivo.registrar(dominio, ownerId, posto.getPathLogico(),
                nomeSanitizado, contentType, payload.length, sha,
                idResponsavel, tipoResponsavel);
        Arquivo salvo = arquivoDAO.salvar(a);
        LOG.log(Level.INFO,
                "[ArquivoServiceImpl] Arquivo anexado dominio={0} owner={1} path={2} resp={3}/{4}",
                new Object[]{dominio, ownerId, posto.getPathLogico(), idResponsavel, tipoResponsavel});
        return salvo;
    }

    private void autorizarLeitura(Arquivo a, Long idSolic, TipoResponsavel tpSolic) {
        if (tpSolic == null) {
            throw new IllegalStateException("Sem permissão.");
        }
        switch (tpSolic) {
            case ADMIN:
            case ENFERMEIRO:
                return;
            case MEDICO:
                if (a.getIdConsulta() != null) {
                    Consulta c = consultaDAO.buscarPorIdComMedico(a.getIdConsulta());
                    if (c != null && c.getMedico() != null && c.getMedico().getId().equals(idSolic)) return;
                }
                if (a.getIdAnotacao() != null) {
                    ConsultaAnotacao an = requererAnotacao(a.getIdAnotacao());
                    if (an.getConsulta() != null && an.getConsulta().getMedico() != null
                            && an.getConsulta().getMedico().getId().equals(idSolic)) return;
                }
                throw new IllegalStateException("Médico não autorizado a baixar este arquivo.");
            case PACIENTE:
                if (a.getIdPaciente() != null && a.getIdPaciente().equals(idSolic)) return;
                if (a.getIdConsulta() != null) {
                    Consulta c = consultaDAO.buscarPorIdComMedico(a.getIdConsulta());
                    if (c != null && c.getPaciente() != null && c.getPaciente().getId().equals(idSolic)) return;
                }
                throw new IllegalStateException("Paciente não autorizado a baixar este arquivo.");
            default:
                throw new IllegalStateException("Tipo de solicitante não suportado.");
        }
    }

    private void validarTipoClinico(TipoResponsavel tp) {
        if (tp == null) {
            throw new IllegalArgumentException("O tipo do responsável é obrigatório.");
        }
        if (!ANEXO_CLINICO.contains(tp)) {
            throw new IllegalStateException(
                    "Apenas médicos, enfermeiros ou administradores podem anexar nesse contexto.");
        }
    }

    private Consulta requererConsulta(Long id) {
        Consulta c = consultaDAO.buscarPorIdComMedico(id);
        if (c == null) {
            throw new IllegalArgumentException("Consulta não encontrada.");
        }
        return c;
    }

    private ConsultaAnotacao requererAnotacao(Long id) {
        ConsultaAnotacao a = anotacaoDAO.buscarPorId(id);
        if (a == null) {
            throw new IllegalArgumentException("Anotação não encontrada.");
        }
        return a;
    }

    private void bloquearSeCancelada(Consulta c) {
        if (c.getStatus() == StatusConsulta.CANCELADA) {
            throw new IllegalStateException("Consulta cancelada não aceita anexos.");
        }
    }

    private static String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static long parseLongEnv(String name, long fallback) {
        String v = System.getenv(name);
        if (v == null || v.trim().isEmpty()) return fallback;
        try {
            return Long.parseLong(v.trim());
        } catch (NumberFormatException nfe) {
            return fallback;
        }
    }
}
