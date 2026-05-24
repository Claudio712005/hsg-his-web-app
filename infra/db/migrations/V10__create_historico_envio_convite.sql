ALTER TABLE hsg.tb_pre_cad_prof
    ADD COLUMN dt_expiracao_convite TIMESTAMP,
    ADD COLUMN qt_envios            INTEGER NOT NULL DEFAULT 0;

CREATE SEQUENCE hsg.seq_env_conv_hist
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_env_conv_hist (
    id_env_conv_hist    BIGINT       NOT NULL DEFAULT nextval('hsg.seq_env_conv_hist'),
    id_pre_cad_prof     BIGINT       NOT NULL,
    id_adm_remetente    BIGINT       NOT NULL,
    nm_adm_remetente    VARCHAR(255) NOT NULL,
    dt_envio            TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_expiracao        TIMESTAMP    NOT NULL,
    st_envio_convite    VARCHAR(10)  NOT NULL DEFAULT 'ENVIADO',
    ds_erro_envio       VARCHAR(1000),

    CONSTRAINT pk_env_conv_hist        PRIMARY KEY (id_env_conv_hist),
    CONSTRAINT fk_hist_pre_cad         FOREIGN KEY (id_pre_cad_prof)
                                       REFERENCES hsg.tb_pre_cad_prof (id_pre_cad_prof),
    CONSTRAINT fk_hist_adm_remetente   FOREIGN KEY (id_adm_remetente)
                                       REFERENCES hsg.tb_adm (id_adm),
    CONSTRAINT ck_st_envio_convite     CHECK (st_envio_convite IN ('ENVIADO', 'ERRO', 'EXPIRADO', 'ACEITO'))
);

CREATE INDEX idx_hist_pre_cad  ON hsg.tb_env_conv_hist (id_pre_cad_prof);
CREATE INDEX idx_hist_st_envio ON hsg.tb_env_conv_hist (st_envio_convite);
CREATE INDEX idx_hist_dt_envio ON hsg.tb_env_conv_hist (dt_envio DESC);
