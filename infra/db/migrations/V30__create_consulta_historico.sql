CREATE SEQUENCE hsg.seq_consulta_historico
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_consulta_historico (
    id_consulta_historico BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_consulta_historico'),
    id_consulta           BIGINT       NOT NULL,
    tp_acao               VARCHAR(15)  NOT NULL,
    id_responsavel        BIGINT,
    tp_responsavel        VARCHAR(12)  NOT NULL,
    ds_observacao         VARCHAR(1000),
    dt_acao               TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_consulta_hist_consulta FOREIGN KEY (id_consulta)
        REFERENCES hsg.tb_consulta (id_consulta),
    CONSTRAINT ck_consulta_hist_acao    CHECK (tp_acao IN
        ('AGENDADA','CHECK_IN','REALIZADA','FALTOU','CANCELADA')),
    CONSTRAINT ck_consulta_hist_resp    CHECK (tp_responsavel IN
        ('PACIENTE','MEDICO','ENFERMEIRO','ADMIN','SISTEMA'))
);

CREATE INDEX idx_consulta_hist_consulta ON hsg.tb_consulta_historico (id_consulta, dt_acao DESC);
CREATE INDEX idx_consulta_hist_acao     ON hsg.tb_consulta_historico (tp_acao);
