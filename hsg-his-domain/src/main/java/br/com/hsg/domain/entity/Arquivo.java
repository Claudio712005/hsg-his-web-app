package br.com.hsg.domain.entity;

import br.com.hsg.domain.converter.IndicativoStatusConverter;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.enums.StorageDomain;
import br.com.hsg.domain.enums.TipoResponsavel;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "TB_ARQUIVO", schema = "hsg")
public class Arquivo {

    public static final int MAX_PATH_LOGICO   = 500;
    public static final int MAX_NOME_ORIGINAL = 255;
    public static final int MAX_CONTENT_TYPE  = 100;
    public static final int MAX_SHA256        = 64;

    private static final Set<TipoResponsavel> TIPOS_PERMITIDOS = EnumSet.of(
            TipoResponsavel.MEDICO,
            TipoResponsavel.ENFERMEIRO,
            TipoResponsavel.ADMIN,
            TipoResponsavel.PACIENTE
    );

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ARQUIVO")
    @SequenceGenerator(name = "SEQ_ARQUIVO", sequenceName = "SEQ_ARQUIVO", allocationSize = 1)
    @Column(name = "ID_ARQUIVO")
    private Long id;

    @Getter
    @Column(name = "DS_PATH_LOGICO", length = MAX_PATH_LOGICO, nullable = false, unique = true)
    private String pathLogico;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "DS_DOMINIO", length = 30, nullable = false)
    private StorageDomain dominio;

    @Getter
    @Column(name = "DS_NOME_ORIGINAL", length = MAX_NOME_ORIGINAL, nullable = false)
    private String nomeOriginal;

    @Getter
    @Column(name = "DS_CONTENT_TYPE", length = MAX_CONTENT_TYPE, nullable = false)
    private String contentType;

    @Getter
    @Column(name = "NR_TAMANHO_BYTES", nullable = false)
    private long tamanhoBytes;

    @Getter
    @Column(name = "DS_SHA256", length = MAX_SHA256)
    private String sha256;

    @Getter
    @Column(name = "ID_CONSULTA")
    private Long idConsulta;

    @Getter
    @Column(name = "ID_ANOTACAO")
    private Long idAnotacao;

    @Getter
    @Column(name = "ID_PACIENTE")
    private Long idPaciente;

    @Getter
    @Column(name = "ID_RESPONSAVEL", nullable = false)
    private Long idResponsavel;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_RESPONSAVEL", length = 12, nullable = false)
    private TipoResponsavel tipoResponsavel;

    @Getter
    @Column(name = "DT_UPLOAD", nullable = false)
    private LocalDateTime dataUpload;

    @Getter
    @Convert(converter = IndicativoStatusConverter.class)
    @Column(name = "ST_ARQUIVO", length = 1, nullable = false)
    private IndicativoStatus status;

    protected Arquivo() {}

    public static Arquivo registrar(StorageDomain dominio, long ownerId, String pathLogico,
                                     String nomeOriginal, String contentType, long tamanhoBytes,
                                     String sha256,
                                     Long idResponsavel, TipoResponsavel tipoResponsavel) {
        if (dominio == null) {
            throw new IllegalArgumentException("O domínio é obrigatório.");
        }
        if (ownerId <= 0L) {
            throw new IllegalArgumentException("ownerId deve ser positivo.");
        }
        if (pathLogico == null || pathLogico.trim().isEmpty()) {
            throw new IllegalArgumentException("O path lógico é obrigatório.");
        }
        if (pathLogico.length() > MAX_PATH_LOGICO) {
            throw new IllegalArgumentException(
                    "O path lógico deve ter no máximo " + MAX_PATH_LOGICO + " caracteres.");
        }
        if (nomeOriginal == null || nomeOriginal.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome original é obrigatório.");
        }
        if (nomeOriginal.length() > MAX_NOME_ORIGINAL) {
            throw new IllegalArgumentException(
                    "O nome original deve ter no máximo " + MAX_NOME_ORIGINAL + " caracteres.");
        }
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("O Content-Type é obrigatório.");
        }
        if (contentType.length() > MAX_CONTENT_TYPE) {
            throw new IllegalArgumentException(
                    "O Content-Type deve ter no máximo " + MAX_CONTENT_TYPE + " caracteres.");
        }
        if (tamanhoBytes <= 0L) {
            throw new IllegalArgumentException("O tamanho deve ser positivo.");
        }
        if (sha256 != null && sha256.length() > MAX_SHA256) {
            throw new IllegalArgumentException("SHA-256 deve ter no máximo " + MAX_SHA256 + " caracteres.");
        }
        if (idResponsavel == null) {
            throw new IllegalArgumentException("O responsável é obrigatório.");
        }
        if (tipoResponsavel == null || !TIPOS_PERMITIDOS.contains(tipoResponsavel)) {
            throw new IllegalArgumentException(
                    "Tipo de responsável inválido. Permitidos: " + TIPOS_PERMITIDOS);
        }

        Arquivo a = new Arquivo();
        a.dominio          = dominio;
        a.pathLogico       = pathLogico.trim();
        a.nomeOriginal     = nomeOriginal.trim();
        a.contentType      = contentType.trim();
        a.tamanhoBytes     = tamanhoBytes;
        a.sha256           = (sha256 == null || sha256.trim().isEmpty()) ? null : sha256.trim();
        a.idResponsavel    = idResponsavel;
        a.tipoResponsavel  = tipoResponsavel;
        a.dataUpload       = LocalDateTime.now();
        a.status           = IndicativoStatus.A;

        switch (dominio) {
            case ANEXO_CLIENTE:
                a.idPaciente = ownerId;
                break;
            case ANEXO_CONSULTA:
            case EXAME_CONSULTA:
                a.idConsulta = ownerId;
                break;
            case ANEXO_ANOTACAO:
                a.idAnotacao = ownerId;
                break;
            default:
                throw new IllegalArgumentException("Domínio não suportado: " + dominio);
        }
        return a;
    }

    public void inativar() {
        this.status = IndicativoStatus.I;
    }
}
