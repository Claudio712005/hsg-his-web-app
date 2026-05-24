package br.com.hsg.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "TB_CONTA_USU", schema = "hsg")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONTA_USU")
    @SequenceGenerator(name = "SEQ_CONTA_USU", sequenceName = "SEQ_CONTA_USU", allocationSize = 1)
    @Column(name = "ID_CONTA_USU")
    private Long id;

    @Column(name = "ID_KCL_USU", unique = true, nullable = false)
    private String keycloakId;

    @Column(name = "NM_USU",  nullable = false, length = 100)
    private String username;
}
