ALTER TABLE hsg.tb_medico
    ADD COLUMN IF NOT EXISTS nr_valor_consulta NUMERIC(10,2);

UPDATE hsg.tb_medico SET nr_valor_consulta = 250.00
WHERE nr_valor_consulta IS NULL;
