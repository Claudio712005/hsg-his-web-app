package br.com.hsg.domain.entity;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PAC_MEDICAO", schema = "hsg")
public class MedicaoPaciente {

    @Getter
    @Id
    @Column(name = "ID_DADOS_PAC", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DADOS_CLINICOS_PAC")
    @SequenceGenerator(name = "SEQ_DADOS_CLINICOS_PAC", sequenceName = "SEQ_DADOS_CLINICOS_PAC", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PAC")
    private Paciente paciente;

    @Getter
    @Column(name = "VL_PESO")
    private Double peso;

    @Getter
    @Column(name = "VL_ALTURA")
    private Double altura;

    @Getter
    @Column(name = "DT_MEDICAO")
    private LocalDateTime dataMedicao;
}
