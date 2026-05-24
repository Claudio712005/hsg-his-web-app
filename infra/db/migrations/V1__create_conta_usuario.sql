CREATE SEQUENCE hsg.seq_conta_usu
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_conta_usu (
    id_conta_usu BIGINT PRIMARY KEY DEFAULT nextval('hsg.seq_conta_usu'),

    id_kcl_usu VARCHAR(64) NOT NULL UNIQUE,
    nm_usu VARCHAR(100) NOT NULL
);

CREATE INDEX idx_conta_usuario_kcl
    ON hsg.tb_conta_usu (id_kcl_usu);