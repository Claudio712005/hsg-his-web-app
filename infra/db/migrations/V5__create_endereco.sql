CREATE SEQUENCE hsg.seq_endereco
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_endereco (
    id_endereco         BIGINT      PRIMARY KEY DEFAULT nextval('hsg.seq_endereco'),
    id_pac              BIGINT      NOT NULL UNIQUE,
    ds_logradouro       VARCHAR(200),
    nr_endereco         VARCHAR(10),
    ds_complemento      VARCHAR(100),
    ds_bairro           VARCHAR(100),
    ds_cidade           VARCHAR(100),
    sg_estado           VARCHAR(2),
    nr_cep              VARCHAR(8),
    dt_cad_end          TIMESTAMP,
    dt_ult_atu_end      TIMESTAMP,

    CONSTRAINT fk_end_pac
        FOREIGN KEY (id_pac)
        REFERENCES hsg.tb_pac (id_pac)
);

CREATE INDEX idx_end_pac ON hsg.tb_endereco (id_pac);
