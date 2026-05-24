CREATE SEQUENCE hsg.seq_tp_sang
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_tp_sang (
    id_tp_sang          BIGINT      PRIMARY KEY DEFAULT nextval('hsg.seq_tp_sang'),

    id_pac              BIGINT      NOT NULL,

    tp_sang             VARCHAR(6),

    ds_tp_sang          VARCHAR(500) NOT NULL,

    st_valid_tp_sang    VARCHAR(1)  NOT NULL,

    dt_cad_tp_sang      TIMESTAMP,
    dt_atu_tp_sang      TIMESTAMP,

    CONSTRAINT fk_tp_sang_pac
        FOREIGN KEY (id_pac)
        REFERENCES hsg.tb_pac (id_pac)
);

CREATE INDEX idx_tp_sang_pac ON hsg.tb_tp_sang (id_pac);

CREATE SEQUENCE hsg.seq_dados_clinicos_pac
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_pac_medicao (
    id_dados_pac    BIGINT          PRIMARY KEY DEFAULT nextval('hsg.seq_dados_clinicos_pac'),

    id_pac          BIGINT          NOT NULL,

    vl_peso         NUMERIC(6, 2),
    vl_altura       NUMERIC(4, 2),

    dt_medicao      TIMESTAMP,

    CONSTRAINT fk_medicao_pac
        FOREIGN KEY (id_pac)
        REFERENCES hsg.tb_pac (id_pac)
);

CREATE INDEX idx_medicao_pac ON hsg.tb_pac_medicao (id_pac);
CREATE INDEX idx_medicao_dt  ON hsg.tb_pac_medicao (id_pac, dt_medicao DESC);