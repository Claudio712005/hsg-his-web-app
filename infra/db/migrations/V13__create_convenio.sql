CREATE SEQUENCE hsg.seq_conv
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_conv (
    id_conv        BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_conv'),
    nm_conv        VARCHAR(150) NOT NULL UNIQUE,
    ds_conv        VARCHAR(500),
    nr_reg_ans     VARCHAR(20),
    nr_cnpj        VARCHAR(20),
    ds_site        VARCHAR(200),
    nr_tel_conv    VARCHAR(20),
    st_conv        CHAR(1)      NOT NULL DEFAULT 'A',
    dt_cad_conv    TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_ult_atu_conv TIMESTAMP
);

CREATE INDEX idx_conv_nome ON hsg.tb_conv (nm_conv);
CREATE INDEX idx_conv_status ON hsg.tb_conv (st_conv);
