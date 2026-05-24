CREATE SEQUENCE hsg.seq_agenda_medica
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_agenda_medica (
    id_agenda_medica BIGINT      PRIMARY KEY DEFAULT nextval('hsg.seq_agenda_medica'),
    id_medico        BIGINT      NOT NULL,
    nr_dia_semana    INTEGER     NOT NULL,
    hr_inicio        TIME        NOT NULL,
    hr_fim           TIME        NOT NULL,
    nr_duracao_min   INTEGER     NOT NULL,
    st_ativo         CHAR(1)     NOT NULL DEFAULT 'A',
    dt_cadastro      TIMESTAMP   NOT NULL DEFAULT NOW(),
    dt_ult_atu       TIMESTAMP,
    CONSTRAINT fk_agenda_medica_medico FOREIGN KEY (id_medico)
        REFERENCES hsg.tb_medico (id_medico),
    CONSTRAINT ck_agenda_dia_semana    CHECK (nr_dia_semana BETWEEN 1 AND 7),
    CONSTRAINT ck_agenda_horario       CHECK (hr_fim > hr_inicio),
    CONSTRAINT ck_agenda_duracao       CHECK (nr_duracao_min > 0)
);

CREATE INDEX idx_agenda_medica_medico ON hsg.tb_agenda_medica (id_medico);
CREATE INDEX idx_agenda_medica_dia    ON hsg.tb_agenda_medica (nr_dia_semana);
CREATE INDEX idx_agenda_medica_status ON hsg.tb_agenda_medica (st_ativo);
