CREATE SEQUENCE hsg.seq_solic_conv
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_solic_conv (
    id_solic_conv      BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_solic_conv'),
    id_pac             BIGINT       NOT NULL,
    id_pl_conv         BIGINT       NOT NULL,
    nr_cart_enc        VARCHAR(255),
    nr_cart_masc       VARCHAR(20),
    dt_validade        DATE,
    tp_titular         VARCHAR(12)  NOT NULL DEFAULT 'TITULAR',
    ds_motivo          VARCHAR(500),
    snp_plano_atual    VARCHAR(200),
    sit_solic_conv     CHAR(1)      NOT NULL DEFAULT 'P',
    id_aprovador       BIGINT,
    ds_mot_rejeicao    VARCHAR(500),
    dt_cad_solic_conv  TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_aprovacao       TIMESTAMP,
    dt_ult_atu_solic_conv TIMESTAMP,
    CONSTRAINT fk_solic_conv_pac FOREIGN KEY (id_pac)
        REFERENCES hsg.tb_pac (id_pac),
    CONSTRAINT fk_solic_conv_pl FOREIGN KEY (id_pl_conv)
        REFERENCES hsg.tb_pl_conv (id_pl_conv)
);

CREATE INDEX idx_solic_conv_pac ON hsg.tb_solic_conv (id_pac);
CREATE INDEX idx_solic_conv_pl ON hsg.tb_solic_conv (id_pl_conv);
CREATE INDEX idx_solic_conv_sit ON hsg.tb_solic_conv (sit_solic_conv);
