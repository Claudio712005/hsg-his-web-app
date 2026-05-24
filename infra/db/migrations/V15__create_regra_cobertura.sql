CREATE SEQUENCE hsg.seq_reg_cob
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_reg_cob (
    id_reg_cob        BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_reg_cob'),
    id_pl_conv        BIGINT       NOT NULL,
    ds_procedimento   VARCHAR(200) NOT NULL,
    ds_categoria      VARCHAR(80),
    nr_carencia_dias  INTEGER      NOT NULL DEFAULT 0,
    vl_pct_copag      NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    fl_cobertura      CHAR(1)      NOT NULL DEFAULT 'S',
    ds_observacao     VARCHAR(500),
    st_reg_cob        CHAR(1)      NOT NULL DEFAULT 'A',
    dt_cad_reg_cob    TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_ult_atu_reg_cob TIMESTAMP,
    CONSTRAINT fk_reg_cob_pl FOREIGN KEY (id_pl_conv)
        REFERENCES hsg.tb_pl_conv (id_pl_conv),
    CONSTRAINT uk_reg_cob_proc UNIQUE (id_pl_conv, ds_procedimento),
    CONSTRAINT ck_reg_cob_carencia CHECK (nr_carencia_dias >= 0),
    CONSTRAINT ck_reg_cob_copag CHECK (vl_pct_copag >= 0 AND vl_pct_copag <= 100)
);

CREATE INDEX idx_reg_cob_plano ON hsg.tb_reg_cob (id_pl_conv);
CREATE INDEX idx_reg_cob_status ON hsg.tb_reg_cob (st_reg_cob);
CREATE INDEX idx_reg_cob_categoria ON hsg.tb_reg_cob (ds_categoria);
