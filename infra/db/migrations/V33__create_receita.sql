CREATE SEQUENCE hsg.seq_receita
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE hsg.seq_receita_item
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_receita (
    id_receita     BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_receita'),
    id_consulta    BIGINT       NOT NULL,
    id_medico      BIGINT       NOT NULL,
    dt_emissao     TIMESTAMP    NOT NULL DEFAULT NOW(),
    st_receita     CHAR(1)      NOT NULL DEFAULT 'A',
    CONSTRAINT fk_rec_consulta FOREIGN KEY (id_consulta)
        REFERENCES hsg.tb_consulta(id_consulta),
    CONSTRAINT fk_rec_medico   FOREIGN KEY (id_medico)
        REFERENCES hsg.tb_medico(id_medico),
    CONSTRAINT ck_rec_status   CHECK (st_receita IN ('A','I'))
);

-- Apenas 1 receita ATIVA por consulta (reemissão inativa anterior + cria nova)
CREATE UNIQUE INDEX uq_rec_consulta_ativa
    ON hsg.tb_receita(id_consulta) WHERE st_receita = 'A';

CREATE INDEX idx_rec_consulta ON hsg.tb_receita(id_consulta, dt_emissao DESC);
CREATE INDEX idx_rec_medico   ON hsg.tb_receita(id_medico, dt_emissao DESC);

CREATE TABLE hsg.tb_receita_item (
    id_receita_item   BIGINT        PRIMARY KEY DEFAULT nextval('hsg.seq_receita_item'),
    id_receita        BIGINT        NOT NULL,
    ds_medicamento    VARCHAR(300)  NOT NULL,
    ds_posologia      VARCHAR(500)  NOT NULL,
    ds_observacao     VARCHAR(1000),
    ds_cid_10         VARCHAR(10),
    nr_ordem          INTEGER       NOT NULL DEFAULT 1,
    CONSTRAINT fk_recitem_receita FOREIGN KEY (id_receita)
        REFERENCES hsg.tb_receita(id_receita) ON DELETE CASCADE,
    CONSTRAINT ck_recitem_ordem   CHECK (nr_ordem >= 1)
);

CREATE INDEX idx_recitem_receita ON hsg.tb_receita_item(id_receita, nr_ordem);
