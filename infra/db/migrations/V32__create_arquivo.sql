CREATE SEQUENCE hsg.seq_arquivo
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_arquivo (
    id_arquivo         BIGINT        PRIMARY KEY DEFAULT nextval('hsg.seq_arquivo'),
    ds_path_logico     VARCHAR(500)  NOT NULL UNIQUE,
    ds_dominio         VARCHAR(30)   NOT NULL,
    ds_nome_original   VARCHAR(255)  NOT NULL,
    ds_content_type    VARCHAR(100)  NOT NULL,
    nr_tamanho_bytes   BIGINT        NOT NULL,
    ds_sha256          VARCHAR(64),
    id_consulta        BIGINT,
    id_anotacao        BIGINT,
    id_paciente        BIGINT,
    id_responsavel     BIGINT        NOT NULL,
    tp_responsavel     VARCHAR(12)   NOT NULL,
    dt_upload          TIMESTAMP     NOT NULL DEFAULT NOW(),
    st_arquivo         CHAR(1)       NOT NULL DEFAULT 'A',
    CONSTRAINT fk_arq_consulta FOREIGN KEY (id_consulta)
        REFERENCES hsg.tb_consulta(id_consulta),
    CONSTRAINT fk_arq_anotacao FOREIGN KEY (id_anotacao)
        REFERENCES hsg.tb_consulta_anotacao(id_consulta_anotacao),
    CONSTRAINT fk_arq_paciente FOREIGN KEY (id_paciente)
        REFERENCES hsg.tb_pac(id_pac),
    CONSTRAINT ck_arq_dominio  CHECK (ds_dominio IN
        ('ANEXO_CLIENTE','ANEXO_CONSULTA','ANEXO_ANOTACAO','EXAME_CONSULTA')),
    CONSTRAINT ck_arq_resp     CHECK (tp_responsavel IN
        ('MEDICO','ENFERMEIRO','ADMIN','PACIENTE')),
    CONSTRAINT ck_arq_tamanho  CHECK (nr_tamanho_bytes > 0),
    CONSTRAINT ck_arq_status   CHECK (st_arquivo IN ('A','I')),
    CONSTRAINT ck_arq_target   CHECK (
        id_consulta IS NOT NULL OR id_anotacao IS NOT NULL OR id_paciente IS NOT NULL)
);

CREATE INDEX idx_arq_consulta ON hsg.tb_arquivo(id_consulta, dt_upload DESC);
CREATE INDEX idx_arq_anotacao ON hsg.tb_arquivo(id_anotacao, dt_upload DESC);
CREATE INDEX idx_arq_paciente ON hsg.tb_arquivo(id_paciente, dt_upload DESC);
CREATE INDEX idx_arq_dominio  ON hsg.tb_arquivo(ds_dominio, st_arquivo);
