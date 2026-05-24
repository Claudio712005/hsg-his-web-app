CREATE SEQUENCE hsg.seq_pre_cad_prof
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_pre_cad_prof (
    id_pre_cad_prof      BIGINT       NOT NULL DEFAULT nextval('hsg.seq_pre_cad_prof'),
    tp_profissional      VARCHAR(12)  NOT NULL,
    nm_profissional      VARCHAR(255) NOT NULL,
    ds_email_prof        VARCHAR(255) NOT NULL,
    nr_cpf_prof          VARCHAR(14)  NOT NULL,

    nr_crm_prof          VARCHAR(20),
    uf_crm_prof          CHAR(2),
    ds_especialidade_prof VARCHAR(100),

    nr_coren_prof        VARCHAR(20),
    uf_coren_prof        CHAR(2),
    cat_coren_prof       VARCHAR(3),

    id_adm_criador       BIGINT       NOT NULL,
    dt_criacao           TIMESTAMP    NOT NULL DEFAULT NOW(),
    st_pre_cad           VARCHAR(10)  NOT NULL DEFAULT 'PENDENTE',
    token_convite        VARCHAR(36)  NOT NULL,
    fl_email_enviado     BOOLEAN      NOT NULL DEFAULT FALSE,
    dt_envio_email       TIMESTAMP,

    CONSTRAINT pk_pre_cad_prof        PRIMARY KEY (id_pre_cad_prof),
    CONSTRAINT uq_token_convite       UNIQUE (token_convite),
    CONSTRAINT fk_pre_cad_adm_criador FOREIGN KEY (id_adm_criador)
                                      REFERENCES hsg.tb_adm (id_adm),
    CONSTRAINT ck_tp_profissional     CHECK (tp_profissional IN ('MEDICO', 'ENFERMEIRO')),
    CONSTRAINT ck_st_pre_cad          CHECK (st_pre_cad IN ('PENDENTE', 'CONCLUIDO', 'EXPIRADO'))
);

CREATE INDEX idx_pre_cad_email    ON hsg.tb_pre_cad_prof (ds_email_prof);
CREATE INDEX idx_pre_cad_cpf      ON hsg.tb_pre_cad_prof (nr_cpf_prof);
CREATE INDEX idx_pre_cad_token    ON hsg.tb_pre_cad_prof (token_convite);
CREATE INDEX idx_pre_cad_status   ON hsg.tb_pre_cad_prof (st_pre_cad);
CREATE INDEX idx_pre_cad_tipo     ON hsg.tb_pre_cad_prof (tp_profissional);
