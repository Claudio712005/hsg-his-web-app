CREATE SEQUENCE hsg.seq_agenda_medica_excecao
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_agenda_medica_excecao (
    id_agenda_excecao BIGINT      PRIMARY KEY DEFAULT nextval('hsg.seq_agenda_medica_excecao'),
    id_medico         BIGINT      NOT NULL,
    dt_inicio         TIMESTAMP   NOT NULL,
    dt_fim            TIMESTAMP   NOT NULL,
    ds_motivo         VARCHAR(300),
    tp_excecao        VARCHAR(20) NOT NULL,
    dt_cadastro       TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_agenda_excecao_medico FOREIGN KEY (id_medico)
        REFERENCES hsg.tb_medico (id_medico),
    CONSTRAINT ck_agenda_excecao_periodo CHECK (dt_fim > dt_inicio),
    CONSTRAINT ck_agenda_excecao_tipo    CHECK (tp_excecao IN ('FERIAS', 'BLOQUEIO', 'EVENTO'))
);

CREATE INDEX idx_agenda_excecao_medico ON hsg.tb_agenda_medica_excecao (id_medico);
CREATE INDEX idx_agenda_excecao_inicio ON hsg.tb_agenda_medica_excecao (dt_inicio);
CREATE INDEX idx_agenda_excecao_tipo   ON hsg.tb_agenda_medica_excecao (tp_excecao);
