CREATE SEQUENCE hsg.seq_consulta
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_consulta (
    id_consulta             BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_consulta'),
    id_paciente             BIGINT       NOT NULL,
    id_medico               BIGINT       NOT NULL,
    id_especialidade        BIGINT,
    id_agenda_slot          BIGINT       NOT NULL,
    id_paciente_convenio    BIGINT,

    tp_atendimento          VARCHAR(12)  NOT NULL,
    st_consulta             VARCHAR(15)  NOT NULL DEFAULT 'AGENDADA',

    dt_consulta             TIMESTAMP    NOT NULL,
    dt_cancelamento         TIMESTAMP,
    ds_cancelamento         VARCHAR(500),

    vl_consulta             NUMERIC(10,2),
    vl_copagamento          NUMERIC(10,2),
    vl_cobertura_convenio   NUMERIC(10,2),

    dt_cadastro             TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_ult_atu              TIMESTAMP,

    CONSTRAINT fk_consulta_paciente         FOREIGN KEY (id_paciente)
        REFERENCES hsg.tb_pac (id_pac),
    CONSTRAINT fk_consulta_medico           FOREIGN KEY (id_medico)
        REFERENCES hsg.tb_medico (id_medico),
    CONSTRAINT fk_consulta_especialidade    FOREIGN KEY (id_especialidade)
        REFERENCES hsg.tb_especialidade (id_especialidade),
    CONSTRAINT fk_consulta_slot             FOREIGN KEY (id_agenda_slot)
        REFERENCES hsg.tb_agenda_medica_slot (id_agenda_slot),
    CONSTRAINT fk_consulta_paciente_conv    FOREIGN KEY (id_paciente_convenio)
        REFERENCES hsg.tb_pac_conv (id_pac_conv),

    CONSTRAINT uk_consulta_slot             UNIQUE (id_agenda_slot),
    CONSTRAINT ck_consulta_tp_atend         CHECK (tp_atendimento IN ('CONVENIO', 'PARTICULAR')),
    CONSTRAINT ck_consulta_status           CHECK (st_consulta IN ('AGENDADA', 'CONFIRMADA', 'REALIZADA', 'CANCELADA', 'FALTOU'))
);

CREATE INDEX idx_consulta_paciente ON hsg.tb_consulta (id_paciente);
CREATE INDEX idx_consulta_medico   ON hsg.tb_consulta (id_medico);
CREATE INDEX idx_consulta_data     ON hsg.tb_consulta (dt_consulta);
CREATE INDEX idx_consulta_status   ON hsg.tb_consulta (st_consulta);
