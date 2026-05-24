package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.StatusValidacao;
import br.com.hsg.domain.enums.TipoSanguineoEnum;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_TP_SANG", schema = "hsg")
public class TipoSanguineo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_TP_SANG", sequenceName = "SEQ_TP_SANG", allocationSize = 1)
    @Column(name = "ID_TP_SANG", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PAC")
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_SANG", length = 6)
    private TipoSanguineoEnum tipoSanguineo;

    @Column(name = "DS_TP_SANG", nullable = false, length = 500)
    private String descricaoPaciente;

    @Column(name = "ST_VALID_TP_SANG", nullable = false, length = 1)
    private StatusValidacao statusValidacao;

    @Column(name = "DT_CAD_TP_SANG")
    private LocalDateTime dataCadastroPaciente;

    @Column(name = "DT_ATU_TP_SANG")
    private LocalDateTime dataAtualizacaoPaciente;

    public TipoSanguineoEnum getTipoSanguineo() {
        return tipoSanguineo;
    }
}
