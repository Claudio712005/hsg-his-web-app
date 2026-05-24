CREATE SEQUENCE hsg.seq_pl_conv
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_pl_conv (
    id_pl_conv        BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_pl_conv'),
    id_conv           BIGINT       NOT NULL,
    nm_pl_conv        VARCHAR(150) NOT NULL,
    cd_pl_conv        VARCHAR(50),
    ds_pl_conv        VARCHAR(500),
    tp_cobertura      VARCHAR(20)  NOT NULL,
    vl_mensalidade    NUMERIC(10,2),
    fl_acomod_individual CHAR(1)   NOT NULL DEFAULT 'N',
    st_pl_conv        CHAR(1)      NOT NULL DEFAULT 'A',
    dt_cad_pl_conv    TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_ult_atu_pl_conv TIMESTAMP,
    CONSTRAINT fk_pl_conv_conv FOREIGN KEY (id_conv)
        REFERENCES hsg.tb_conv (id_conv),
    CONSTRAINT uk_pl_conv_nome UNIQUE (id_conv, nm_pl_conv)
);

CREATE INDEX idx_pl_conv_conv ON hsg.tb_pl_conv (id_conv);
CREATE INDEX idx_pl_conv_status ON hsg.tb_pl_conv (st_pl_conv);
CREATE INDEX idx_pl_conv_cobertura ON hsg.tb_pl_conv (tp_cobertura);
