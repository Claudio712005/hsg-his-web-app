package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.AcaoAlergia;
import br.com.hsg.domain.enums.GravidadeAlergia;
import br.com.hsg.domain.enums.StatusAlergia;
import br.com.hsg.domain.enums.TipoAlergia;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ALRG_HIST", schema = "hsg")
public class AlergiaHistorico {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ALRG_HIST")
    @SequenceGenerator(name = "SEQ_ALRG_HIST", sequenceName = "SEQ_ALRG_HIST", allocationSize = 1)
    @Column(name = "ID_ALRG_HIST")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALRG", nullable = false)
    private Alergia alergia;

    @Getter
    @Column(name = "ID_USR_HIST")
    private Long idUsuario;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "ACAO_HIST", length = 10, nullable = false)
    private AcaoAlergia acao;

    @Getter
    @Column(name = "NM_ALRG_SNAP", length = 100)
    private String nomeSnap;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_ALRG_SNAP", length = 2)
    private TipoAlergia tipoSnap;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_GRAV_SNAP", length = 1)
    private GravidadeAlergia gravidadeSnap;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "ST_ALRG_SNAP", length = 10)
    private StatusAlergia statusSnap;

    @Getter
    @Column(name = "DT_ACAO_HIST", nullable = false)
    private LocalDateTime dataAcao;

    protected AlergiaHistorico() {}

    public static AlergiaHistorico registrar(Alergia alergia, Long idUsuario, AcaoAlergia acao) {
        AlergiaHistorico h = new AlergiaHistorico();
        h.alergia      = alergia;
        h.idUsuario    = idUsuario;
        h.acao         = acao;
        h.nomeSnap     = alergia.getNome();
        h.tipoSnap     = alergia.getTipoAlergia();
        h.gravidadeSnap = alergia.getGravidadeAlergia();
        h.statusSnap   = alergia.getStatusAlergia();
        h.dataAcao     = LocalDateTime.now();
        return h;
    }
}
