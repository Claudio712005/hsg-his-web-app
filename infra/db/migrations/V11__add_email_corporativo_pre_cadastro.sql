ALTER TABLE hsg.tb_pre_cad_prof
    RENAME COLUMN ds_email_prof TO ds_email_pessoal;

ALTER TABLE hsg.tb_pre_cad_prof
    ADD COLUMN ds_email_corp VARCHAR(255);

ALTER TABLE hsg.tb_pre_cad_prof
    ADD CONSTRAINT uq_pre_cad_email_corp UNIQUE (ds_email_corp);

CREATE INDEX idx_pre_cad_email_corp
    ON hsg.tb_pre_cad_prof (ds_email_corp);
