CREATE SEQUENCE hsg.seq_pac_conv
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_pac_conv (
    id_pac_conv       BIGINT      PRIMARY KEY DEFAULT nextval('hsg.seq_pac_conv'),
    id_pac            BIGINT      NOT NULL,
    id_pl_conv        BIGINT      NOT NULL,
    nr_cart_hash      VARCHAR(64),
    nr_cart_enc       VARCHAR(255),
    nr_cart_masc      VARCHAR(20),
    dt_validade       DATE,
    tp_titular        VARCHAR(12) NOT NULL DEFAULT 'TITULAR',
    id_aprovador      BIGINT,
    dt_adesao         TIMESTAMP   NOT NULL DEFAULT NOW(),
    dt_cancelamento   TIMESTAMP,
    st_pac_conv       CHAR(1)     NOT NULL DEFAULT 'A',
    dt_cad_pac_conv   TIMESTAMP   NOT NULL DEFAULT NOW(),
    dt_ult_atu_pac_conv TIMESTAMP,
    CONSTRAINT fk_pac_conv_pac FOREIGN KEY (id_pac)
        REFERENCES hsg.tb_pac (id_pac),
    CONSTRAINT fk_pac_conv_pl FOREIGN KEY (id_pl_conv)
        REFERENCES hsg.tb_pl_conv (id_pl_conv)
);

CREATE UNIQUE INDEX uk_pac_conv_ativo
    ON hsg.tb_pac_conv (id_pac)
    WHERE st_pac_conv = 'A';

CREATE INDEX idx_pac_conv_pac ON hsg.tb_pac_conv (id_pac);
CREATE INDEX idx_pac_conv_pl ON hsg.tb_pac_conv (id_pl_conv);
CREATE INDEX idx_pac_conv_status ON hsg.tb_pac_conv (st_pac_conv);
