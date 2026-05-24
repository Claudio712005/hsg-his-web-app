CREATE SEQUENCE hsg.seq_adm
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_adm (
    id_adm              BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_adm'),
    frt_nm_adm          VARCHAR(100),
    lst_nm_adm          VARCHAR(150),
    ds_email_adm        VARCHAR(255),
    st_adm              CHAR(1)      NOT NULL DEFAULT 'A',
    dt_cad_adm          TIMESTAMP    NOT NULL DEFAULT NOW(),
    id_conta_usu_adm    BIGINT       UNIQUE,
    CONSTRAINT fk_adm_conta_usu
        FOREIGN KEY (id_conta_usu_adm)
        REFERENCES hsg.tb_conta_usu (id_conta_usu)
);
