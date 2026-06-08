CREATE SEQUENCE hsg.seq_consulta_anotacao
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_consulta_anotacao (
    id_consulta_anotacao BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_consulta_anotacao'),
    id_consulta          BIGINT       NOT NULL,
    ds_titulo            VARCHAR(200) NOT NULL,
    ds_descricao         VARCHAR(2000) NOT NULL,
    id_responsavel       BIGINT       NOT NULL,
    tp_responsavel       VARCHAR(12)  NOT NULL,
    dt_criacao           TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_consulta_anot_consulta FOREIGN KEY (id_consulta)
        REFERENCES hsg.tb_consulta (id_consulta),
    CONSTRAINT ck_consulta_anot_resp     CHECK (tp_responsavel IN
        ('MEDICO','ENFERMEIRO','ADMIN'))
);

CREATE INDEX idx_consulta_anot_consulta ON hsg.tb_consulta_anotacao (id_consulta, dt_criacao DESC);
