CREATE SEQUENCE hsg.seq_pac
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_pac (
    id_pac BIGINT PRIMARY KEY DEFAULT nextval('hsg.seq_pac'),

    frt_nm_pac VARCHAR(100),
    lst_nm_pac VARCHAR(150),

    ds_email VARCHAR(255),

    nr_cpf_hash VARCHAR(64) NOT NULL UNIQUE,
    nr_cpf_enc VARCHAR(255) NOT NULL,
    nr_rg_enc VARCHAR(255),

    dt_nasc_pac DATE,
    nr_tel VARCHAR(20),

    st_pac CHAR(1) NOT NULL,

    dt_cad_pac TIMESTAMP NOT NULL,
    dt_ult_atu TIMESTAMP,

    id_conta_usu BIGINT,

    CONSTRAINT fk_pac_conta_usuario
        FOREIGN KEY (id_conta_usu)
        REFERENCES hsg.tb_conta_usu (id_conta_usu)
);

CREATE INDEX idx_pac_nome
    ON hsg.tb_pac (frt_nm_pac, lst_nm_pac);

CREATE INDEX idx_pac_email
    ON hsg.tb_pac (ds_email);

CREATE INDEX idx_pac_status
    ON hsg.tb_pac (st_pac);