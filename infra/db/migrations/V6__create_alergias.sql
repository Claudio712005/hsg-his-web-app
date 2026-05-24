CREATE SEQUENCE hsg.seq_alrg
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_alrg (
    id_alrg         BIGINT          NOT NULL DEFAULT nextval('hsg.seq_alrg'),
    id_pac          BIGINT          NOT NULL,
    nm_alrg         VARCHAR(100)    NOT NULL,
    ds_alrg         VARCHAR(500),
    tp_alrg         VARCHAR(2)      NOT NULL,
    tp_grav_alrg    VARCHAR(1)      NOT NULL,
    st_alergia      VARCHAR(10)     NOT NULL DEFAULT 'INFORMADA',
    obs_alrg        VARCHAR(500),
    obs_enf_alrg    VARCHAR(500),
    ds_reacao       VARCHAR(500),
    id_cad_alrg     BIGINT,
    id_apr_alrg     BIGINT,
    dt_ult_reacao   TIMESTAMP,
    dt_cad_alrg     TIMESTAMP       NOT NULL DEFAULT NOW(),
    dt_ult_atu_alrg TIMESTAMP,
    CONSTRAINT pk_alrg PRIMARY KEY (id_alrg),
    CONSTRAINT fk_alrg_pac FOREIGN KEY (id_pac) REFERENCES hsg.tb_pac (id_pac)
);

CREATE SEQUENCE hsg.seq_alrg_hist
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_alrg_hist (
    id_alrg_hist    BIGINT          NOT NULL DEFAULT nextval('hsg.seq_alrg_hist'),
    id_alrg         BIGINT          NOT NULL,
    id_usr_hist     BIGINT,
    acao_hist       VARCHAR(10)     NOT NULL,
    nm_alrg_snap    VARCHAR(100),
    tp_alrg_snap    VARCHAR(2),
    tp_grav_snap    VARCHAR(1),
    st_alrg_snap    VARCHAR(10),
    dt_acao_hist    TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_alrg_hist PRIMARY KEY (id_alrg_hist),
    CONSTRAINT fk_alrg_hist_alrg FOREIGN KEY (id_alrg) REFERENCES hsg.tb_alrg (id_alrg)
);
