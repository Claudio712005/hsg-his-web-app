CREATE SEQUENCE hsg.seq_especialidade
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_especialidade (
    id_especialidade    BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_especialidade'),
    nm_especialidade    VARCHAR(100) NOT NULL UNIQUE,
    ds_especialidade    VARCHAR(500),
    area_especialidade  VARCHAR(50),
    st_especialidade    CHAR(1)      NOT NULL DEFAULT 'A',
    dt_cad_especialidade TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE SEQUENCE hsg.seq_medico
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_medico (
    id_medico               BIGINT      NOT NULL DEFAULT nextval('hsg.seq_medico'),
    frt_nm_medico           VARCHAR(100),
    lst_nm_medico           VARCHAR(150),
    ds_email_medico         VARCHAR(255),
    nr_tel_medico           VARCHAR(20),
    nr_crm                  VARCHAR(20),
    uf_crm                  CHAR(2),
    nr_cpf_hash_medico      VARCHAR(64)  UNIQUE,
    nr_cpf_enc_medico       VARCHAR(255),
    dt_nasc_medico          DATE,
    id_especialidade        BIGINT,
    st_medico               CHAR(1)      NOT NULL DEFAULT 'A',
    dt_cad_medico           TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_ult_atu_medico       TIMESTAMP,
    id_conta_usu_medico     BIGINT       UNIQUE,
    CONSTRAINT pk_medico            PRIMARY KEY (id_medico),
    CONSTRAINT fk_medico_conta_usu  FOREIGN KEY (id_conta_usu_medico)
                                    REFERENCES hsg.tb_conta_usu (id_conta_usu),
    CONSTRAINT fk_medico_especialidade FOREIGN KEY (id_especialidade)
                                    REFERENCES hsg.tb_especialidade (id_especialidade)
);
