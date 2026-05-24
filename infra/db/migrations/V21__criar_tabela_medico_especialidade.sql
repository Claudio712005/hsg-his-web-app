CREATE SEQUENCE hsg.seq_medico_especialidade
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_medico_especialidade (
    id_medico_especialidade BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_medico_especialidade'),
    id_medico               BIGINT       NOT NULL,
    id_especialidade        BIGINT       NOT NULL,
    st_principal            CHAR(1)      NOT NULL DEFAULT 'N',
    dt_cadastro             TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_med_esp_medico        FOREIGN KEY (id_medico)
        REFERENCES hsg.tb_medico (id_medico),
    CONSTRAINT fk_med_esp_especialidade FOREIGN KEY (id_especialidade)
        REFERENCES hsg.tb_especialidade (id_especialidade),
    CONSTRAINT uk_med_esp UNIQUE (id_medico, id_especialidade)
);

CREATE INDEX idx_med_esp_medico        ON hsg.tb_medico_especialidade (id_medico);
CREATE INDEX idx_med_esp_especialidade ON hsg.tb_medico_especialidade (id_especialidade);

INSERT INTO hsg.tb_medico_especialidade (id_medico, id_especialidade, st_principal, dt_cadastro)
SELECT m.id_medico, m.id_especialidade, 'S', NOW()
FROM hsg.tb_medico m
WHERE m.id_especialidade IS NOT NULL
ON CONFLICT (id_medico, id_especialidade) DO NOTHING;

ALTER TABLE hsg.tb_medico
    ADD COLUMN IF NOT EXISTS nr_duracao_consulta_min INTEGER;

ALTER TABLE hsg.tb_especialidade
    ADD COLUMN IF NOT EXISTS nr_duracao_padrao_min INTEGER DEFAULT 30;
