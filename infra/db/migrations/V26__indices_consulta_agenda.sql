ALTER TABLE hsg.tb_agenda_medica_slot
    ADD CONSTRAINT fk_slot_consulta FOREIGN KEY (id_consulta)
        REFERENCES hsg.tb_consulta (id_consulta);

CREATE INDEX IF NOT EXISTS idx_consulta_medico_data
    ON hsg.tb_consulta (id_medico, dt_consulta);

CREATE INDEX IF NOT EXISTS idx_consulta_paciente_data
    ON hsg.tb_consulta (id_paciente, dt_consulta DESC);

CREATE INDEX IF NOT EXISTS idx_slot_medico_status
    ON hsg.tb_agenda_medica_slot (id_medico, st_slot, dt_inicio);
