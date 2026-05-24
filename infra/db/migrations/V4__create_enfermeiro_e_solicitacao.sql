CREATE SEQUENCE hsg.seq_enfer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_enfer (
    id_enfer               BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_enfer'),
    frt_nm_enfer           VARCHAR(100),
    lst_nm_enfer           VARCHAR(150),
    ds_email_enfer         VARCHAR(255),
    nr_tel_enfer           VARCHAR(20),
    nr_coren               VARCHAR(20),
    uf_coren               CHAR(2),
    cat_coren              VARCHAR(3),
    nr_cpf_hash_enfer      VARCHAR(64)  UNIQUE,
    nr_cpf_enc_enfer       VARCHAR(255),
    dt_nasc_enfer          DATE,
    ds_especialidade_enfer VARCHAR(100),
    ds_setor_enfer         VARCHAR(100),
    st_enfer               CHAR(1)      NOT NULL DEFAULT 'A',
    dt_cad_enfer           TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_ult_atu_enfer       TIMESTAMP,
    id_conta_usu_enfer     BIGINT       UNIQUE,
    CONSTRAINT fk_enfer_conta_usu
        FOREIGN KEY (id_conta_usu_enfer)
        REFERENCES hsg.tb_conta_usu (id_conta_usu)
);

CREATE SEQUENCE hsg.seq_solic_atl
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_solic_atl (
    id_solic_atl        BIGINT          PRIMARY KEY DEFAULT nextval('hsg.seq_solic_atl'),

    id_pac              BIGINT          NOT NULL,
    id_enfer            BIGINT,

    frt_nm_solic_atl    VARCHAR(100),
    lst_nm_solic_atl    VARCHAR(150),
    ds_email_atl        VARCHAR(255),
    nr_tel_atl          VARCHAR(20),

    snp_nome_completo   VARCHAR(250),
    snp_email           VARCHAR(255),
    snp_tel             VARCHAR(20),

    ds_logr_prop        VARCHAR(200),
    nr_prop             VARCHAR(10),
    ds_compl_prop       VARCHAR(100),
    ds_bairro_prop      VARCHAR(100),
    ds_cidade_prop      VARCHAR(100),
    sg_estado_prop      VARCHAR(2),
    nr_cep_prop         VARCHAR(8),

    snp_logr            VARCHAR(200),
    snp_nr              VARCHAR(10),
    snp_compl           VARCHAR(100),
    snp_bairro          VARCHAR(100),
    snp_cidade          VARCHAR(100),
    snp_estado          VARCHAR(2),
    snp_cep             VARCHAR(8),

    vl_peso_prop        DOUBLE PRECISION,
    vl_altura_prop      DOUBLE PRECISION,
    tp_sang_prop        VARCHAR(10),

    snp_peso            DOUBLE PRECISION,
    snp_altura          DOUBLE PRECISION,
    snp_tp_sang         VARCHAR(10),

    ds_mot_cancel       VARCHAR(500),
    id_cancelador       BIGINT,
    tp_cancelador       VARCHAR(10),
    dt_cancelamento     TIMESTAMP,

    ds_motivo           VARCHAR(500),

    tp_solic            VARCHAR(10)     NOT NULL DEFAULT 'CADASTRAL',
    sit_apr_solic       CHAR(1)         NOT NULL DEFAULT 'P',

    dt_cad_solic_atl    TIMESTAMP       NOT NULL,
    dt_ult_atu          TIMESTAMP,

    CONSTRAINT fk_solic_pac
        FOREIGN KEY (id_pac)
        REFERENCES hsg.tb_pac (id_pac),

    CONSTRAINT fk_solic_enfer
        FOREIGN KEY (id_enfer)
        REFERENCES hsg.tb_enfer (id_enfer),

    CONSTRAINT ck_sit_apr_solic
        CHECK (sit_apr_solic IN ('P', 'A', 'R', 'C'))
);

CREATE INDEX idx_solic_pac    ON hsg.tb_solic_atl (id_pac);
CREATE INDEX idx_solic_status ON hsg.tb_solic_atl (sit_apr_solic);
CREATE INDEX idx_solic_dt     ON hsg.tb_solic_atl (id_pac, dt_cad_solic_atl DESC);
CREATE INDEX idx_solic_tipo   ON hsg.tb_solic_atl (tp_solic);
