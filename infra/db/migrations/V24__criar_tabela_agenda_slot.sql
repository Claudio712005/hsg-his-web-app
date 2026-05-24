CREATE SEQUENCE hsg.seq_agenda_medica_slot
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_agenda_medica_slot (
    id_agenda_slot   BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_agenda_medica_slot'),
    id_medico        BIGINT       NOT NULL,
    id_especialidade BIGINT,
    dt_inicio        TIMESTAMP    NOT NULL,
    dt_fim           TIMESTAMP    NOT NULL,
    st_slot          VARCHAR(12)  NOT NULL DEFAULT 'LIVRE',
    id_consulta      BIGINT,
    dt_cadastro      TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_ult_atu       TIMESTAMP,
    CONSTRAINT fk_slot_medico        FOREIGN KEY (id_medico)
        REFERENCES hsg.tb_medico (id_medico),
    CONSTRAINT fk_slot_especialidade FOREIGN KEY (id_especialidade)
        REFERENCES hsg.tb_especialidade (id_especialidade),
    CONSTRAINT uk_slot_medico_inicio UNIQUE (id_medico, dt_inicio),
    CONSTRAINT ck_slot_periodo       CHECK (dt_fim > dt_inicio),
    CONSTRAINT ck_slot_status        CHECK (st_slot IN ('LIVRE', 'RESERVADO', 'BLOQUEADO', 'CANCELADO'))
);

CREATE INDEX idx_slot_medico_data ON hsg.tb_agenda_medica_slot (id_medico, dt_inicio);
CREATE INDEX idx_slot_status      ON hsg.tb_agenda_medica_slot (st_slot);
CREATE INDEX idx_slot_inicio      ON hsg.tb_agenda_medica_slot (dt_inicio);
