-- ── Administradores ──────────────────────────────────────────────────
INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'ad000000-0000-0000-0000-000000000001', 'admin.hsg');

INSERT INTO hsg.tb_adm (id_adm, frt_nm_adm, lst_nm_adm, ds_email_adm, st_adm, dt_cad_adm, id_conta_usu_adm)
VALUES (nextval('hsg.seq_adm'), 'Administrador', 'HSG', 'admin@hsg.com.br', 'A', NOW(),
        currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'ad000000-0000-0000-0000-000000000002', 'admin.sandra');

INSERT INTO hsg.tb_adm (id_adm, frt_nm_adm, lst_nm_adm, ds_email_adm, st_adm, dt_cad_adm, id_conta_usu_adm)
VALUES (nextval('hsg.seq_adm'), 'Sandra', 'Vieira', 'sandra.vieira@hsg.com.br', 'A', NOW(),
        currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'ad000000-0000-0000-0000-000000000003', 'admin.marcos');

INSERT INTO hsg.tb_adm (id_adm, frt_nm_adm, lst_nm_adm, ds_email_adm, st_adm, dt_cad_adm, id_conta_usu_adm)
VALUES (nextval('hsg.seq_adm'), 'Marcos', 'Teixeira', 'marcos.teixeira@hsg.com.br', 'A', NOW(),
        currval('hsg.seq_conta_usu'));

-- ── Especialidades ────────────────────────────────────────────────────
INSERT INTO hsg.tb_especialidade (id_especialidade, nm_especialidade, ds_especialidade, area_especialidade, st_especialidade, dt_cad_especialidade)
VALUES
    (nextval('hsg.seq_especialidade'), 'Clínica Médica',     'Diagnóstico e tratamento de doenças internas.',    'CLINICA',    'A', NOW()),
    (nextval('hsg.seq_especialidade'), 'Pediatria',          'Atenção médica à criança e ao adolescente.',       'CLINICA',    'A', NOW()),
    (nextval('hsg.seq_especialidade'), 'Cardiologia',        'Diagnóstico e tratamento de doenças do coração.',  'CLINICA',    'A', NOW()),
    (nextval('hsg.seq_especialidade'), 'Ortopedia',          'Sistema musculoesquelético.',                      'CIRURGIA',   'A', NOW()),
    (nextval('hsg.seq_especialidade'), 'Neurologia',         'Sistema nervoso central e periférico.',            'CLINICA',    'A', NOW()),
    (nextval('hsg.seq_especialidade'), 'UTI Adulto',         'Terapia intensiva para adultos.',                  'CLINICA',    'A', NOW()),
    (nextval('hsg.seq_especialidade'), 'Radiologia',         'Diagnóstico por imagem.',                          'DIAGNOSTICO','A', NOW());

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'cf000000-0000-0000-0000-000000000001', 'claudio.filho');

INSERT INTO hsg.tb_pac (id_pac, frt_nm_pac, lst_nm_pac, ds_email,
    nr_cpf_hash, nr_cpf_enc, nr_rg_enc, dt_nasc_pac, nr_tel, st_pac, dt_cad_pac, id_conta_usu)
VALUES (nextval('hsg.seq_pac'), 'Cláudio', 'Filho', 'clausilvaaraujo11@gmail.com',
    'claudio_filho_dev_cpf_placeholder_0000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_CLAUDIO_FILHO', 'DEV_PLACEHOLDER_RG_ENC_CLAUDIO_FILHO',
    '2005-01-07', '11999990011', 'A', NOW(), currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_tp_sang (id_tp_sang, id_pac, tp_sang, ds_tp_sang, st_valid_tp_sang, dt_cad_tp_sang)
VALUES (nextval('hsg.seq_tp_sang'), currval('hsg.seq_pac'),
    'O_POS', 'Informado pelo paciente no cadastro.', '0', NOW());

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'en000000-0000-0000-0000-000000000001', 'enf.maria');

INSERT INTO hsg.tb_enfer (id_enfer, frt_nm_enfer, lst_nm_enfer, ds_email_enfer, nr_tel_enfer,
    nr_coren, uf_coren, cat_coren, nr_cpf_hash_enfer, nr_cpf_enc_enfer, dt_nasc_enfer,
    ds_especialidade_enfer, ds_setor_enfer, st_enfer, dt_cad_enfer, dt_ult_atu_enfer, id_conta_usu_enfer)
VALUES (nextval('hsg.seq_enfer'), 'Maria', 'Santos', 'maria.santos@hsg.com.br', '11988880022',
    '654321', 'SP', 'ENF',
    'dev_placeholder_cpf_hash_maria_santos_00000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_MARIA_SANTOS', '1990-03-15',
    'UTI Adulto', 'UTI', 'A', NOW(), NOW(), currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'md000000-0000-0000-0000-000000000001', 'dr.joao');

INSERT INTO hsg.tb_medico (id_medico, frt_nm_medico, lst_nm_medico, ds_email_medico, nr_tel_medico,
    nr_crm, uf_crm, nr_cpf_hash_medico, nr_cpf_enc_medico, dt_nasc_medico,
    id_especialidade, st_medico, dt_cad_medico, dt_ult_atu_medico, id_conta_usu_medico)
SELECT nextval('hsg.seq_medico'), 'João', 'Silva', 'joao.silva@hsg.com.br', '11977770033',
    '123456', 'SP',
    'dev_placeholder_cpf_hash_joao_silva_0000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_JOAO_SILVA', '1982-07-20',
    e.id_especialidade, 'A', NOW(), NOW(), currval('hsg.seq_conta_usu')
FROM hsg.tb_especialidade e WHERE e.nm_especialidade = 'Clínica Médica';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg,
    st_alergia, obs_alrg, ds_reacao, id_cad_alrg, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Dipirona',
    'Alergia ao princípio ativo dipirona sódica.', 'M', 'G', 'INFORMADA',
    'Paciente relata reação alérgica grave após uso em 2023.',
    'Urticária generalizada, queda de pressão.', p.id_pac, NOW(), NOW()
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho' LIMIT 1;

INSERT INTO hsg.tb_alrg_hist (id_alrg_hist, id_alrg, id_usr_hist, acao_hist,
    nm_alrg_snap, tp_alrg_snap, tp_grav_snap, st_alrg_snap, dt_acao_hist)
SELECT nextval('hsg.seq_alrg_hist'), a.id_alrg, p.id_pac, 'CRIADA',
    a.nm_alrg, a.tp_alrg, a.tp_grav_alrg, a.st_alergia, NOW()
FROM hsg.tb_alrg a JOIN hsg.tb_pac p ON p.id_pac = a.id_cad_alrg
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho' AND a.nm_alrg = 'Dipirona';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg,
    st_alergia, obs_alrg, ds_reacao, id_cad_alrg, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Amendoim',
    'Alergia alimentar a amendoim e derivados.', 'A', 'A', 'APROVADA',
    'Confirmada por exame de IgE específica.', 'Anafilaxia. Usa EpiPen.',
    p.id_pac, NOW() - INTERVAL '30 days', NOW() - INTERVAL '10 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho' LIMIT 1;

INSERT INTO hsg.tb_alrg_hist (id_alrg_hist, id_alrg, id_usr_hist, acao_hist,
    nm_alrg_snap, tp_alrg_snap, tp_grav_snap, st_alrg_snap, dt_acao_hist)
SELECT nextval('hsg.seq_alrg_hist'), a.id_alrg, p.id_pac, 'CRIADA',
    a.nm_alrg, a.tp_alrg, a.tp_grav_alrg, 'INFORMADA', NOW() - INTERVAL '30 days'
FROM hsg.tb_alrg a JOIN hsg.tb_pac p ON p.id_pac = a.id_cad_alrg
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho' AND a.nm_alrg = 'Amendoim';

INSERT INTO hsg.tb_alrg_hist (id_alrg_hist, id_alrg, id_usr_hist, acao_hist,
    nm_alrg_snap, tp_alrg_snap, tp_grav_snap, st_alrg_snap, dt_acao_hist)
SELECT nextval('hsg.seq_alrg_hist'), a.id_alrg, p.id_pac, 'APROVADA',
    a.nm_alrg, a.tp_alrg, a.tp_grav_alrg, a.st_alergia, NOW() - INTERVAL '10 days'
FROM hsg.tb_alrg a JOIN hsg.tb_pac p ON p.id_pac = a.id_cad_alrg
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho' AND a.nm_alrg = 'Amendoim';

-- ── Médicos adicionais ────────────────────────────────────────────────
INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'md000000-0000-0000-0000-000000000002', 'dr.ana');

INSERT INTO hsg.tb_medico (id_medico, frt_nm_medico, lst_nm_medico, ds_email_medico, nr_tel_medico,
    nr_crm, uf_crm, nr_cpf_hash_medico, nr_cpf_enc_medico, dt_nasc_medico,
    id_especialidade, st_medico, dt_cad_medico, dt_ult_atu_medico, id_conta_usu_medico)
SELECT nextval('hsg.seq_medico'), 'Ana', 'Carvalho', 'ana.carvalho@hsg.com.br', '11977770044',
    '987654', 'SP', 'dev_cpf_hash_ana_carvalho_000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_ANA_CARVALHO', '1985-04-12',
    e.id_especialidade, 'A', NOW() - INTERVAL '180 days', NOW(), currval('hsg.seq_conta_usu')
FROM hsg.tb_especialidade e WHERE e.nm_especialidade = 'Cardiologia';

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'md000000-0000-0000-0000-000000000003', 'dr.roberto');

INSERT INTO hsg.tb_medico (id_medico, frt_nm_medico, lst_nm_medico, ds_email_medico, nr_tel_medico,
    nr_crm, uf_crm, nr_cpf_hash_medico, nr_cpf_enc_medico, dt_nasc_medico,
    id_especialidade, st_medico, dt_cad_medico, dt_ult_atu_medico, id_conta_usu_medico)
SELECT nextval('hsg.seq_medico'), 'Roberto', 'Mendes', 'roberto.mendes@hsg.com.br', '11977770055',
    '876543', 'SP', 'dev_cpf_hash_roberto_mendes_00000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_ROBERTO_MENDES', '1978-11-30',
    e.id_especialidade, 'A', NOW() - INTERVAL '90 days', NOW(), currval('hsg.seq_conta_usu')
FROM hsg.tb_especialidade e WHERE e.nm_especialidade = 'Pediatria';

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'md000000-0000-0000-0000-000000000004', 'dra.fernanda');

INSERT INTO hsg.tb_medico (id_medico, frt_nm_medico, lst_nm_medico, ds_email_medico, nr_tel_medico,
    nr_crm, uf_crm, nr_cpf_hash_medico, nr_cpf_enc_medico, dt_nasc_medico,
    id_especialidade, st_medico, dt_cad_medico, dt_ult_atu_medico, id_conta_usu_medico)
SELECT nextval('hsg.seq_medico'), 'Fernanda', 'Lima', 'fernanda.lima@hsg.com.br', '11977770066',
    '765432', 'SP', 'dev_cpf_hash_fernanda_lima_0000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_FERNANDA_LIMA', '1990-08-25',
    e.id_especialidade, 'A', NOW() - INTERVAL '60 days', NOW(), currval('hsg.seq_conta_usu')
FROM hsg.tb_especialidade e WHERE e.nm_especialidade = 'Neurologia';

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'md000000-0000-0000-0000-000000000005', 'dr.carlos');

INSERT INTO hsg.tb_medico (id_medico, frt_nm_medico, lst_nm_medico, ds_email_medico, nr_tel_medico,
    nr_crm, uf_crm, nr_cpf_hash_medico, nr_cpf_enc_medico, dt_nasc_medico,
    id_especialidade, st_medico, dt_cad_medico, dt_ult_atu_medico, id_conta_usu_medico)
SELECT nextval('hsg.seq_medico'), 'Carlos', 'Oliveira', 'carlos.oliveira@hsg.com.br', '11977770077',
    '654321', 'RJ', 'dev_cpf_hash_carlos_oliveira_000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_CARLOS_OLIVEIRA', '1975-03-18',
    e.id_especialidade, 'A', NOW() - INTERVAL '365 days', NOW(), currval('hsg.seq_conta_usu')
FROM hsg.tb_especialidade e WHERE e.nm_especialidade = 'Ortopedia';

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'md000000-0000-0000-0000-000000000006', 'dra.patricia');

INSERT INTO hsg.tb_medico (id_medico, frt_nm_medico, lst_nm_medico, ds_email_medico, nr_tel_medico,
    nr_crm, uf_crm, nr_cpf_hash_medico, nr_cpf_enc_medico, dt_nasc_medico,
    id_especialidade, st_medico, dt_cad_medico, dt_ult_atu_medico, id_conta_usu_medico)
SELECT nextval('hsg.seq_medico'), 'Patricia', 'Souza', 'patricia.souza@hsg.com.br', '11977770088',
    '543210', 'MG', 'dev_cpf_hash_patricia_souza_0000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_PATRICIA_SOUZA', '1988-06-07',
    e.id_especialidade, 'I', NOW() - INTERVAL '730 days', NOW(), currval('hsg.seq_conta_usu')
FROM hsg.tb_especialidade e WHERE e.nm_especialidade = 'Clínica Médica';

-- ── Enfermeiros adicionais ────────────────────────────────────────────
INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'en000000-0000-0000-0000-000000000002', 'enf.lucas');

INSERT INTO hsg.tb_enfer (id_enfer, frt_nm_enfer, lst_nm_enfer, ds_email_enfer, nr_tel_enfer,
    nr_coren, uf_coren, cat_coren, nr_cpf_hash_enfer, nr_cpf_enc_enfer, dt_nasc_enfer,
    ds_especialidade_enfer, ds_setor_enfer, st_enfer, dt_cad_enfer, dt_ult_atu_enfer, id_conta_usu_enfer)
VALUES (nextval('hsg.seq_enfer'), 'Lucas', 'Ferreira', 'lucas.ferreira@hsg.com.br', '11988880033',
    '222222', 'SP', 'ENF', 'dev_cpf_hash_lucas_ferreira_000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_LUCAS_FERREIRA', '1993-09-20',
    'UTI Adulto', 'UTI', 'A', NOW() - INTERVAL '120 days', NOW(), currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'en000000-0000-0000-0000-000000000003', 'enf.juliana');

INSERT INTO hsg.tb_enfer (id_enfer, frt_nm_enfer, lst_nm_enfer, ds_email_enfer, nr_tel_enfer,
    nr_coren, uf_coren, cat_coren, nr_cpf_hash_enfer, nr_cpf_enc_enfer, dt_nasc_enfer,
    ds_especialidade_enfer, ds_setor_enfer, st_enfer, dt_cad_enfer, dt_ult_atu_enfer, id_conta_usu_enfer)
VALUES (nextval('hsg.seq_enfer'), 'Juliana', 'Alves', 'juliana.alves@hsg.com.br', '11988880044',
    '333333', 'SP', 'ENF', 'dev_cpf_hash_juliana_alves_0000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_JULIANA_ALVES', '1991-02-14',
    'Emergência', 'Pronto-Socorro', 'A', NOW() - INTERVAL '200 days', NOW(), currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'en000000-0000-0000-0000-000000000004', 'enf.ricardo');

INSERT INTO hsg.tb_enfer (id_enfer, frt_nm_enfer, lst_nm_enfer, ds_email_enfer, nr_tel_enfer,
    nr_coren, uf_coren, cat_coren, nr_cpf_hash_enfer, nr_cpf_enc_enfer, dt_nasc_enfer,
    ds_especialidade_enfer, ds_setor_enfer, st_enfer, dt_cad_enfer, dt_ult_atu_enfer, id_conta_usu_enfer)
VALUES (nextval('hsg.seq_enfer'), 'Ricardo', 'Pinto', 'ricardo.pinto@hsg.com.br', '11988880055',
    '444444', 'SP', 'ENF', 'dev_cpf_hash_ricardo_pinto_0000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_RICARDO_PINTO', '1987-07-03',
    'Cirurgia', 'Centro Cirúrgico', 'A', NOW() - INTERVAL '45 days', NOW(), currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'en000000-0000-0000-0000-000000000005', 'enf.camila');

INSERT INTO hsg.tb_enfer (id_enfer, frt_nm_enfer, lst_nm_enfer, ds_email_enfer, nr_tel_enfer,
    nr_coren, uf_coren, cat_coren, nr_cpf_hash_enfer, nr_cpf_enc_enfer, dt_nasc_enfer,
    ds_especialidade_enfer, ds_setor_enfer, st_enfer, dt_cad_enfer, dt_ult_atu_enfer, id_conta_usu_enfer)
VALUES (nextval('hsg.seq_enfer'), 'Camila', 'Rocha', 'camila.rocha@hsg.com.br', '11988880066',
    '555555', 'RJ', 'ENF', 'dev_cpf_hash_camila_rocha_00000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_CAMILA_ROCHA', '1995-12-22',
    'Pediatria', 'UTI Pediátrica', 'A', NOW() - INTERVAL '30 days', NOW(), currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'en000000-0000-0000-0000-000000000006', 'enf.andre');

INSERT INTO hsg.tb_enfer (id_enfer, frt_nm_enfer, lst_nm_enfer, ds_email_enfer, nr_tel_enfer,
    nr_coren, uf_coren, cat_coren, nr_cpf_hash_enfer, nr_cpf_enc_enfer, dt_nasc_enfer,
    ds_especialidade_enfer, ds_setor_enfer, st_enfer, dt_cad_enfer, dt_ult_atu_enfer, id_conta_usu_enfer)
VALUES (nextval('hsg.seq_enfer'), 'André', 'Lima', 'andre.lima@hsg.com.br', '11988880077',
    '666666', 'MG', 'ENF', 'dev_cpf_hash_andre_lima_000000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_ANDRE_LIMA', '1989-05-17',
    'Clínica', 'Clínica Médica', 'I', NOW() - INTERVAL '500 days', NOW(), currval('hsg.seq_conta_usu'));

-- ── Pacientes demo adicionais ─────────────────────────────────────────
INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'cf000000-0000-0000-0000-000000000002', 'mariana.santos');

INSERT INTO hsg.tb_pac (id_pac, frt_nm_pac, lst_nm_pac, ds_email,
    nr_cpf_hash, nr_cpf_enc, nr_rg_enc, dt_nasc_pac, nr_tel, st_pac, dt_cad_pac, id_conta_usu)
VALUES (nextval('hsg.seq_pac'), 'Mariana', 'Santos', 'mariana.santos@example.com',
    'mariana_santos_dev_cpf_placeholder_00000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_MARIANA_SANTOS', 'DEV_PLACEHOLDER_RG_ENC_MARIANA_SANTOS',
    '1992-04-18', '11999990012', 'A', NOW() - INTERVAL '180 days', currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'cf000000-0000-0000-0000-000000000003', 'pedro.oliveira');

INSERT INTO hsg.tb_pac (id_pac, frt_nm_pac, lst_nm_pac, ds_email,
    nr_cpf_hash, nr_cpf_enc, nr_rg_enc, dt_nasc_pac, nr_tel, st_pac, dt_cad_pac, id_conta_usu)
VALUES (nextval('hsg.seq_pac'), 'Pedro', 'Oliveira', 'pedro.oliveira@example.com',
    'pedro_oliveira_dev_cpf_placeholder_00000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_PEDRO_OLIVEIRA', 'DEV_PLACEHOLDER_RG_ENC_PEDRO_OLIVEIRA',
    '1985-11-02', '11999990013', 'A', NOW() - INTERVAL '210 days', currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'cf000000-0000-0000-0000-000000000004', 'carla.silva');

INSERT INTO hsg.tb_pac (id_pac, frt_nm_pac, lst_nm_pac, ds_email,
    nr_cpf_hash, nr_cpf_enc, nr_rg_enc, dt_nasc_pac, nr_tel, st_pac, dt_cad_pac, id_conta_usu)
VALUES (nextval('hsg.seq_pac'), 'Carla', 'Silva', 'carla.silva@example.com',
    'carla_silva_dev_cpf_placeholder_0000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_CARLA_SILVA', 'DEV_PLACEHOLDER_RG_ENC_CARLA_SILVA',
    '1998-07-30', '11999990014', 'A', NOW() - INTERVAL '90 days', currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'cf000000-0000-0000-0000-000000000005', 'joao.lima');

INSERT INTO hsg.tb_pac (id_pac, frt_nm_pac, lst_nm_pac, ds_email,
    nr_cpf_hash, nr_cpf_enc, nr_rg_enc, dt_nasc_pac, nr_tel, st_pac, dt_cad_pac, id_conta_usu)
VALUES (nextval('hsg.seq_pac'), 'João', 'Lima', 'joao.lima@example.com',
    'joao_lima_dev_cpf_placeholder_000000000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_JOAO_LIMA', 'DEV_PLACEHOLDER_RG_ENC_JOAO_LIMA',
    '1979-02-14', '11999990015', 'A', NOW() - INTERVAL '365 days', currval('hsg.seq_conta_usu'));

INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
VALUES (nextval('hsg.seq_conta_usu'), 'cf000000-0000-0000-0000-000000000006', 'beatriz.costa');

INSERT INTO hsg.tb_pac (id_pac, frt_nm_pac, lst_nm_pac, ds_email,
    nr_cpf_hash, nr_cpf_enc, nr_rg_enc, dt_nasc_pac, nr_tel, st_pac, dt_cad_pac, id_conta_usu)
VALUES (nextval('hsg.seq_pac'), 'Beatriz', 'Costa', 'beatriz.costa@example.com',
    'beatriz_costa_dev_cpf_placeholder_00000000000000000000000000',
    'DEV_PLACEHOLDER_CPF_ENC_BEATRIZ_COSTA', 'DEV_PLACEHOLDER_RG_ENC_BEATRIZ_COSTA',
    '2001-09-25', '11999990016', 'A', NOW() - INTERVAL '45 days', currval('hsg.seq_conta_usu'));

-- ── Alergias demo: PENDENTES (status INFORMADA) ───────────────────────
-- Cláudio Filho
INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Penicilina',
    'Suspeita de alergia a penicilinas (amoxicilina inclusive).', 'M', 'G', 'INFORMADA',
    'Mãe relata reação cutânea durante infância.', 'Urticária e edema facial.',
    p.id_pac, NOW() - INTERVAL '5 years', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Pólen sazonal',
    'Rinite alérgica recorrente em períodos de polinização.', 'AM', 'L', 'INFORMADA',
    NULL, 'Espirros, coriza, prurido nasal.',
    p.id_pac, NOW() - INTERVAL '30 days', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho';

-- Mariana
INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Ácaros',
    'Sensibilidade significativa a poeira doméstica.', 'AM', 'M', 'INFORMADA',
    'Crises noturnas frequentes em ambiente fechado.', 'Tosse, congestão nasal, dispneia leve.',
    p.id_pac, NOW() - INTERVAL '10 days', NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'mariana.santos';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Contraste iodado',
    'Reação a contraste iodado em exames de imagem.', 'M', 'G', 'INFORMADA',
    'Reação durante TC com contraste em 2024.', 'Hipotensão, broncoespasmo, urticária.',
    p.id_pac, NOW() - INTERVAL '120 days', NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'mariana.santos';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Soja',
    'Sensibilidade a derivados de soja.', 'A', 'L', 'INFORMADA',
    NULL, 'Desconforto gastrointestinal e prurido oral leve.',
    p.id_pac, NULL, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'mariana.santos';

-- Pedro
INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Aspirina (AAS)',
    'Reação adversa a ácido acetilsalicílico.', 'M', 'M', 'INFORMADA',
    'Reação dermatológica após uso de AAS 500mg.', 'Rash cutâneo, prurido generalizado.',
    p.id_pac, NOW() - INTERVAL '90 days', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'pedro.oliveira';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Camarão',
    'Alergia a crustáceos — camarão e lagosta.', 'A', 'A', 'INFORMADA',
    'Anafilaxia em jantar de família em 2024.', 'Anafilaxia, edema de glote, hipotensão.',
    p.id_pac, NOW() - INTERVAL '180 days', NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'pedro.oliveira';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Pelo de gato',
    'Sensibilidade a epitélio de felinos.', 'AM', 'M', 'INFORMADA',
    NULL, 'Espirros, lacrimejamento, congestão.',
    p.id_pac, NOW() - INTERVAL '20 days', NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'pedro.oliveira';

-- Carla
INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Leite de vaca',
    'Suspeita de alergia a proteína do leite.', 'A', 'M', 'INFORMADA',
    'Sintomas após ingestão de laticínios.', 'Náusea, urticária, dor abdominal.',
    p.id_pac, NOW() - INTERVAL '15 days', NOW() - INTERVAL '1 days', NOW() - INTERVAL '1 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'carla.silva';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Mostarda',
    'Sensibilidade leve a mostarda em preparações.', 'A', 'L', 'INFORMADA',
    NULL, 'Prurido oral e formigamento labial.',
    p.id_pac, NULL, NOW() - INTERVAL '9 days', NOW() - INTERVAL '9 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'carla.silva';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Perfume aerossol',
    'Reação respiratória a aerossóis perfumados.', 'AM', 'L', 'INFORMADA',
    NULL, 'Tosse e irritação nasal.',
    p.id_pac, NOW() - INTERVAL '40 days', NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'carla.silva';

-- João
INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Glúten',
    'Suspeita de alergia ao glúten (não doença celíaca).', 'A', 'M', 'INFORMADA',
    'Aguardando teste sorológico.', 'Distensão, fadiga pós-prandial, urticária.',
    p.id_pac, NOW() - INTERVAL '8 days', NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'joao.lima';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Ibuprofeno',
    'Reação a anti-inflamatórios não esteroidais.', 'M', 'G', 'INFORMADA',
    'Reação após dose de Ibuprofeno 600mg.', 'Angioedema labial, broncoespasmo.',
    p.id_pac, NOW() - INTERVAL '60 days', NOW() - INTERVAL '16 days', NOW() - INTERVAL '16 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'joao.lima';

-- Beatriz
INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Látex',
    'Alergia a luvas e materiais de látex.', 'O', 'G', 'INFORMADA',
    'Reação durante procedimento odontológico.', 'Edema, urticária localizada e dispneia leve.',
    p.id_pac, NOW() - INTERVAL '45 days', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'beatriz.costa';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, ds_reacao, id_cad_alrg, dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Ovo',
    'Sensibilidade a clara de ovo.', 'A', 'M', 'INFORMADA',
    NULL, 'Urticária e diarreia após ingestão.',
    p.id_pac, NOW() - INTERVAL '5 days', NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'beatriz.costa';

-- ── Alergias demo: APROVADAS ──────────────────────────────────────────
INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Picada de abelha',
    'Reação sistêmica grave a venenos de himenópteros.', 'O', 'A', 'APROVADA',
    'Histórico de internação prévia.',
    'Confirmada por dosagem de IgE específica e histórico clínico. Paciente orientado a portar adrenalina auto-injetável.',
    'Anafilaxia, edema laríngeo, hipotensão grave.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '300 days', NOW() - INTERVAL '60 days', NOW() - INTERVAL '40 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '654321'
WHERE c.nm_usu = 'mariana.santos';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Frutos do mar',
    'Alergia a peixes e moluscos diversos.', 'A', 'G', 'APROVADA',
    'Reação após refeição em restaurante.',
    'Confirmada por teste de IgE específica (camarão, lagosta, atum). Dieta de exclusão indicada.',
    'Edema labial, dispneia, broncoespasmo.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '200 days', NOW() - INTERVAL '90 days', NOW() - INTERVAL '70 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '654321'
WHERE c.nm_usu = 'pedro.oliveira';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Sulfas',
    'Reação a antibióticos sulfonamídicos.', 'M', 'G', 'APROVADA',
    'Reação prévia em uso de sulfametoxazol.',
    'Confirmada por reintrodução supervisionada. Documentado em prontuário e prescrição.',
    'Erupção morbiliforme extensa, febre.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '150 days', NOW() - INTERVAL '50 days', NOW() - INTERVAL '20 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '222222'
WHERE c.nm_usu = 'carla.silva';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Pólen gramíneas',
    'Rinite e conjuntivite alérgica sazonal.', 'AM', 'M', 'APROVADA',
    NULL,
    'Confirmada por skin prick test. Imunoterapia em curso.',
    'Espirros em salva, prurido nasal e ocular.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '120 days', NOW() - INTERVAL '180 days', NOW() - INTERVAL '95 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '333333'
WHERE c.nm_usu = 'joao.lima';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Níquel',
    'Dermatite de contato a bijuterias e fivelas.', 'O', 'L', 'APROVADA',
    'Lesão recorrente no pulso e pescoço.',
    'Dermatite de contato alérgica confirmada por patch test.',
    'Eczema crônico em áreas de contato.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '30 days', NOW() - INTERVAL '35 days', NOW() - INTERVAL '25 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '444444'
WHERE c.nm_usu = 'beatriz.costa';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Aspirina (AAS) — confirmada',
    'Reação confirmada a AAS e AINEs em geral.', 'M', 'G', 'APROVADA',
    'Histórico documentado em internação anterior.',
    'Hipersensibilidade a AAS e AINEs confirmada. Evitar prescrição da classe.',
    'Broncoespasmo, urticária facial, edema palpebral.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '400 days', NOW() - INTERVAL '120 days', NOW() - INTERVAL '110 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '654321'
WHERE c.nm_usu = 'claudio.filho';

-- ── Alergias demo: REJEITADAS ─────────────────────────────────────────
INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Chocolate',
    'Suposta alergia a chocolate.', 'A', 'L', 'REJEITADA',
    'Sintomas após ingestão.',
    'Sintomas compatíveis com intolerância digestiva, não alergia. Manter ingestão moderada conforme tolerância.',
    'Desconforto abdominal leve, sem manifestação cutânea.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '90 days', NOW() - INTERVAL '40 days', NOW() - INTERVAL '30 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '222222'
WHERE c.nm_usu = 'mariana.santos';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Detergente comum',
    'Reação após contato com detergente doméstico.', 'O', 'L', 'REJEITADA',
    'Hiperemia em mãos após manipulação prolongada.',
    'Quadro compatível com dermatite de contato irritativa, não alérgica. Recomendado uso de luvas.',
    'Vermelhidão e ressecamento sem prurido intenso.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '40 days', NOW() - INTERVAL '25 days', NOW() - INTERVAL '18 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '333333'
WHERE c.nm_usu = 'pedro.oliveira';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Tomate',
    'Suspeita de alergia ao tomate cru.', 'A', 'L', 'REJEITADA',
    'Coceira ocasional na boca após consumo cru.',
    'Sem evidência clínica de alergia IgE-mediada. Possível síndrome de alergia oral leve associada a pólen — segura ingestão cozido.',
    'Prurido oral transitório.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '60 days', NOW() - INTERVAL '20 days', NOW() - INTERVAL '12 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '444444'
WHERE c.nm_usu = 'carla.silva';

INSERT INTO hsg.tb_alrg (id_alrg, id_pac, nm_alrg, ds_alrg, tp_alrg, tp_grav_alrg, st_alergia,
    obs_alrg, obs_enf_alrg, ds_reacao, id_cad_alrg, id_apr_alrg,
    dt_ult_reacao, dt_cad_alrg, dt_ult_atu_alrg)
SELECT nextval('hsg.seq_alrg'), p.id_pac, 'Poeira comum',
    'Sintomas respiratórios atribuídos a poeira.', 'AM', 'L', 'REJEITADA',
    'Episódios de rinite em ambientes variados.',
    'Quadro respiratório melhor explicado por rinite vasomotora. Sem sensibilização a aeroalérgenos no skin prick test.',
    'Coriza e espirros sem padrão alérgico definido.',
    p.id_pac, e.id_enfer,
    NOW() - INTERVAL '50 days', NOW() - INTERVAL '30 days', NOW() - INTERVAL '15 days'
FROM hsg.tb_pac p
JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
JOIN hsg.tb_enfer e ON e.nr_coren = '222222'
WHERE c.nm_usu = 'beatriz.costa';

-- ── Histórico das alergias demo: ação CRIADA para todas ───────────────
INSERT INTO hsg.tb_alrg_hist (id_alrg_hist, id_alrg, id_usr_hist, acao_hist,
    nm_alrg_snap, tp_alrg_snap, tp_grav_snap, st_alrg_snap, dt_acao_hist)
SELECT nextval('hsg.seq_alrg_hist'), a.id_alrg, a.id_cad_alrg, 'CRIADA',
    a.nm_alrg, a.tp_alrg, a.tp_grav_alrg, 'INFORMADA', a.dt_cad_alrg
FROM hsg.tb_alrg a
WHERE a.id_alrg > 2;

-- ── Histórico das aprovadas/rejeitadas: ação na avaliação ─────────────
INSERT INTO hsg.tb_alrg_hist (id_alrg_hist, id_alrg, id_usr_hist, acao_hist,
    nm_alrg_snap, tp_alrg_snap, tp_grav_snap, st_alrg_snap, dt_acao_hist)
SELECT nextval('hsg.seq_alrg_hist'), a.id_alrg, a.id_apr_alrg,
    CASE WHEN a.st_alergia = 'APROVADA' THEN 'APROVADA' ELSE 'REJEITADA' END,
    a.nm_alrg, a.tp_alrg, a.tp_grav_alrg, a.st_alergia, a.dt_ult_atu_alrg
FROM hsg.tb_alrg a
WHERE a.st_alergia IN ('APROVADA', 'REJEITADA')
  AND a.id_apr_alrg IS NOT NULL
  AND a.id_alrg > 2;

-- ══════════════════════════════════════════════════════════════════════
-- ── Massa de teste: convênios (pacientes + vínculos ativos) ───────────
-- ══════════════════════════════════════════════════════════════════════

-- 30 contas de usuário para pacientes de teste em massa
INSERT INTO hsg.tb_conta_usu (id_conta_usu, id_kcl_usu, nm_usu)
SELECT nextval('hsg.seq_conta_usu'),
       'massa000-0000-0000-0000-' || lpad(g::text, 12, '0'),
       'paciente.massa' || lpad(g::text, 3, '0')
FROM generate_series(1, 30) AS g;

-- 30 pacientes de teste (CPF hash = md5 do username, garante unicidade)
INSERT INTO hsg.tb_pac (id_pac, frt_nm_pac, lst_nm_pac, ds_email,
    nr_cpf_hash, nr_cpf_enc, dt_nasc_pac, nr_tel, st_pac, dt_cad_pac, id_conta_usu)
SELECT nextval('hsg.seq_pac'),
       (ARRAY['Ana','Bruno','Carla','Diego','Elena','Felipe','Gabriela','Hugo','Iara','João',
              'Karen','Lucas','Marina','Nuno','Olívia','Paulo','Quésia','Rafael','Sofia','Tiago',
              'Úrsula','Victor','Wagner','Xênia','Yuri','Zélia','Alice','Breno','Clara','Davi'])[(substr(cu.nm_usu, 15)::int)],
       'Teste ' || substr(cu.nm_usu, 15),
       cu.nm_usu || '@example.com',
       md5(cu.nm_usu),
       'DEV_ENC_' || cu.nm_usu,
       DATE '1960-01-01' + (substr(cu.nm_usu, 15)::int * 311),
       '1198' || lpad(((substr(cu.nm_usu, 15)::int * 137) % 100000000)::text, 8, '0'),
       'A',
       NOW() - ((substr(cu.nm_usu, 15)::int * 7) % 400) * INTERVAL '1 day',
       cu.id_conta_usu
FROM hsg.tb_conta_usu cu
WHERE cu.nm_usu LIKE 'paciente.massa%';

-- Vínculo ativo (1:1) para cada paciente de massa, distribuído round-robin entre planos ativos
WITH planos AS (
    SELECT id_pl_conv,
           (row_number() OVER (ORDER BY id_pl_conv) - 1) AS rn,
           count(*) OVER () AS total
    FROM hsg.tb_pl_conv
    WHERE st_pl_conv = 'A'
),
pac_massa AS (
    SELECT p.id_pac,
           (row_number() OVER (ORDER BY p.id_pac) - 1) AS seq
    FROM hsg.tb_pac p
    JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = p.id_conta_usu
    WHERE cu.nm_usu LIKE 'paciente.massa%'
)
INSERT INTO hsg.tb_pac_conv (id_pac_conv, id_pac, id_pl_conv, nr_cart_hash, nr_cart_enc,
    nr_cart_masc, dt_validade, tp_titular, id_aprovador, dt_adesao, st_pac_conv, dt_cad_pac_conv)
SELECT nextval('hsg.seq_pac_conv'), pm.id_pac, pl.id_pl_conv,
       md5('cart' || pm.id_pac), 'DEV_CART_ENC_' || pm.id_pac,
       '****' || lpad(((pm.id_pac * 73) % 10000)::text, 4, '0'),
       CURRENT_DATE + (180 + (pm.id_pac * 11) % 600) * INTERVAL '1 day',
       CASE WHEN pm.seq % 5 = 0 THEN 'DEPENDENTE' ELSE 'TITULAR' END,
       (SELECT id_adm FROM hsg.tb_adm ORDER BY id_adm LIMIT 1),
       NOW() - ((pm.id_pac * 13) % 350) * INTERVAL '1 day',
       'A', NOW()
FROM pac_massa pm
JOIN planos pl ON pl.rn = (pm.seq % pl.total);

-- Solicitações de convênio: histórico (APROVADAS e REJEITADAS) para os primeiros pacientes de massa
WITH alvo AS (
    SELECT p.id_pac, (row_number() OVER (ORDER BY p.id_pac)) AS seq
    FROM hsg.tb_pac p
    JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = p.id_conta_usu
    WHERE cu.nm_usu LIKE 'paciente.massa%'
    ORDER BY p.id_pac
    LIMIT 8
),
plano_alvo AS (
    SELECT id_pl_conv, (row_number() OVER (ORDER BY id_pl_conv DESC) - 1) AS rn,
           count(*) OVER () AS total
    FROM hsg.tb_pl_conv WHERE st_pl_conv = 'A'
)
INSERT INTO hsg.tb_solic_conv (id_solic_conv, id_pac, id_pl_conv, nr_cart_enc, nr_cart_masc,
    dt_validade, tp_titular, ds_motivo, snp_plano_atual, sit_solic_conv, id_aprovador,
    ds_mot_rejeicao, dt_cad_solic_conv, dt_aprovacao, dt_ult_atu_solic_conv)
SELECT nextval('hsg.seq_solic_conv'), a.id_pac, pa.id_pl_conv,
       'DEV_CART_ENC_SOLIC_' || a.id_pac, '****' || lpad(((a.id_pac * 51) % 10000)::text, 4, '0'),
       CURRENT_DATE + 365 * INTERVAL '1 day', 'TITULAR',
       'Solicitação de teste (histórico).',
       NULL,
       CASE WHEN a.seq % 2 = 0 THEN 'A' ELSE 'R' END,
       (SELECT id_adm FROM hsg.tb_adm ORDER BY id_adm LIMIT 1),
       CASE WHEN a.seq % 2 = 0 THEN NULL ELSE 'Dados da carteirinha não conferem com a operadora.' END,
       NOW() - (a.seq + 10) * INTERVAL '1 day',
       CASE WHEN a.seq % 2 = 0 THEN NOW() - (a.seq + 5) * INTERVAL '1 day' ELSE NULL END,
       NOW() - (a.seq + 5) * INTERVAL '1 day'
FROM alvo a
JOIN plano_alvo pa ON pa.rn = (a.seq % pa.total);

-- ── Convênios ativos para pacientes nomeados (testes de agendamento) ──
-- claudio.filho: convênio ativo há 200 dias (carências curtas já liberadas)
INSERT INTO hsg.tb_pac_conv (id_pac_conv, id_pac, id_pl_conv, nr_cart_hash, nr_cart_enc,
    nr_cart_masc, dt_validade, tp_titular, id_aprovador, dt_adesao, st_pac_conv, dt_cad_pac_conv)
SELECT nextval('hsg.seq_pac_conv'), p.id_pac,
       (SELECT id_pl_conv FROM hsg.tb_pl_conv WHERE st_pl_conv = 'A' ORDER BY id_pl_conv LIMIT 1),
       md5('cart_claudio'), 'DEV_CART_ENC_CLAUDIO', '****1234',
       CURRENT_DATE + 365 * INTERVAL '1 day', 'TITULAR',
       (SELECT id_adm FROM hsg.tb_adm ORDER BY id_adm LIMIT 1),
       NOW() - 200 * INTERVAL '1 day', 'A', NOW()
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho';

-- mariana.santos: convênio ativo há 10 dias (procedimentos com carência longa ainda bloqueados)
INSERT INTO hsg.tb_pac_conv (id_pac_conv, id_pac, id_pl_conv, nr_cart_hash, nr_cart_enc,
    nr_cart_masc, dt_validade, tp_titular, id_aprovador, dt_adesao, st_pac_conv, dt_cad_pac_conv)
SELECT nextval('hsg.seq_pac_conv'), p.id_pac,
       (SELECT id_pl_conv FROM hsg.tb_pl_conv WHERE st_pl_conv = 'A' ORDER BY id_pl_conv DESC LIMIT 1),
       md5('cart_mariana'), 'DEV_CART_ENC_MARIANA', '****5678',
       CURRENT_DATE + 365 * INTERVAL '1 day', 'TITULAR',
       (SELECT id_adm FROM hsg.tb_adm ORDER BY id_adm LIMIT 1),
       NOW() - 10 * INTERVAL '1 day', 'A', NOW()
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'mariana.santos';

-- ══════════════════════════════════════════════════════════════════════
-- ── Massa de teste: agenda médica (grades + exceções demo) ────────────
-- ══════════════════════════════════════════════════════════════════════

-- Duração padrão de consulta dos médicos demo
UPDATE hsg.tb_medico SET nr_duracao_consulta_min = 30
WHERE nr_duracao_consulta_min IS NULL;

-- Valor de consulta particular por médico demo
UPDATE hsg.tb_medico m SET nr_valor_consulta = v.valor
FROM (VALUES
    ('dr.joao',      200.00),
    ('dr.ana',       350.00),
    ('dr.roberto',   220.00),
    ('dra.fernanda', 400.00),
    ('dr.carlos',    300.00)
) AS v(usuario, valor)
JOIN hsg.tb_conta_usu cu ON cu.nm_usu = v.usuario
WHERE m.id_conta_usu_medico = cu.id_conta_usu;

UPDATE hsg.tb_medico SET nr_valor_consulta = 250.00 WHERE nr_valor_consulta IS NULL;

-- ── Especialidades principais (N:N, st_principal = 'S') ──────────────
INSERT INTO hsg.tb_medico_especialidade (id_medico, id_especialidade, st_principal, dt_cadastro)
SELECT m.id_medico, m.id_especialidade, 'S', NOW()
FROM hsg.tb_medico m
WHERE m.id_especialidade IS NOT NULL
ON CONFLICT (id_medico, id_especialidade) DO NOTHING;

-- ── Especialidades secundárias (N:N, st_principal = 'N') ──────────────
-- Dr. João (Clínica Médica) → também atende Pediatria
INSERT INTO hsg.tb_medico_especialidade (id_medico, id_especialidade, st_principal, dt_cadastro)
SELECT m.id_medico, e.id_especialidade, 'N', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_especialidade e ON e.nm_especialidade = 'Pediatria'
WHERE cu.nm_usu = 'dr.joao'
ON CONFLICT (id_medico, id_especialidade) DO NOTHING;

-- Dra. Fernanda (Neurologia) → também atende Clínica Médica
INSERT INTO hsg.tb_medico_especialidade (id_medico, id_especialidade, st_principal, dt_cadastro)
SELECT m.id_medico, e.id_especialidade, 'N', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_especialidade e ON e.nm_especialidade = 'Clínica Médica'
WHERE cu.nm_usu = 'dra.fernanda'
ON CONFLICT (id_medico, id_especialidade) DO NOTHING;

-- Dr. Carlos (Ortopedia) → também atende Clínica Médica
INSERT INTO hsg.tb_medico_especialidade (id_medico, id_especialidade, st_principal, dt_cadastro)
SELECT m.id_medico, e.id_especialidade, 'N', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_especialidade e ON e.nm_especialidade = 'Clínica Médica'
WHERE cu.nm_usu = 'dr.carlos'
ON CONFLICT (id_medico, id_especialidade) DO NOTHING;

-- ── Grades semanais (TB_AGENDA_MEDICA) ────────────────────────────────
-- dow: 1=Seg, 2=Ter, 3=Qua, 4=Qui, 5=Sex, 6=Sab, 7=Dom

-- Dr. João Silva (Clínica Médica)
--   Seg/Qua/Sex manhã 08:00-12:00 (slot 30) + Seg/Qua tarde 14:00-18:00 (slot 30)
INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, dow, TIME '08:00', TIME '12:00', 30, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (VALUES (1), (3), (5)) AS d(dow)
WHERE cu.nm_usu = 'dr.joao';

INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, dow, TIME '14:00', TIME '18:00', 30, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (VALUES (1), (3)) AS d(dow)
WHERE cu.nm_usu = 'dr.joao';

-- Dra. Ana Carvalho (Cardiologia)
--   Ter/Qui 14:00-18:00 (slot 40) + Sex 08:00-11:00 (slot 30)
INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, dow, TIME '14:00', TIME '18:00', 40, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (VALUES (2), (4)) AS d(dow)
WHERE cu.nm_usu = 'dr.ana';

INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, 5, TIME '08:00', TIME '11:00', 30, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.ana';

-- Dr. Roberto Mendes (Pediatria)
--   Seg-Sex 09:00-11:00 (slot 20) + Sáb 09:00-12:00 (slot 30)
INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, dow, TIME '09:00', TIME '11:00', 20, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (VALUES (1), (2), (3), (4), (5)) AS d(dow)
WHERE cu.nm_usu = 'dr.roberto';

INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, 6, TIME '09:00', TIME '12:00', 30, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.roberto';

-- Dra. Fernanda Lima (Neurologia)
--   Ter/Qui manhã 09:00-12:00 (slot 45) + Seg tarde 13:00-17:00 (slot 45)
INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, dow, TIME '09:00', TIME '12:00', 45, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (VALUES (2), (4)) AS d(dow)
WHERE cu.nm_usu = 'dra.fernanda';

INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, 1, TIME '13:00', TIME '17:00', 45, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dra.fernanda';

-- Dr. Carlos Oliveira (Ortopedia)
--   Seg/Qua/Sex tarde 14:00-18:00 (slot 30) + Sáb manhã 08:00-12:00 (slot 30)
INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, dow, TIME '14:00', TIME '18:00', 30, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (VALUES (1), (3), (5)) AS d(dow)
WHERE cu.nm_usu = 'dr.carlos';

INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, 6, TIME '08:00', TIME '12:00', 30, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.carlos';

-- Grade inativada (histórica) para dr.joao — Sáb 09:00-12:00 (não atende mais)
INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, 6, TIME '09:00', TIME '12:00', 30, 'I', NOW() - INTERVAL '120 days'
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.joao';

-- ── Exceções (TB_AGENDA_MEDICA_EXCECAO) ───────────────────────────────
-- Cobre passado, presente e futuro; tipos FERIAS / BLOQUEIO / EVENTO

-- Dr. João — férias futuras (7 dias, daqui a 15 dias)
INSERT INTO hsg.tb_agenda_medica_excecao (id_medico, dt_inicio, dt_fim, ds_motivo, tp_excecao, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE + INTERVAL '15 days')::timestamp,
       (CURRENT_DATE + INTERVAL '22 days')::timestamp,
       'Férias programadas — viagem em família',
       'FERIAS', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.joao';

-- Dra. Ana — bloqueio futuro (congresso, 2 dias)
INSERT INTO hsg.tb_agenda_medica_excecao (id_medico, dt_inicio, dt_fim, ds_motivo, tp_excecao, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE + INTERVAL '10 days')::date + TIME '08:00',
       (CURRENT_DATE + INTERVAL '12 days')::date + TIME '18:00',
       'Congresso Brasileiro de Cardiologia',
       'EVENTO', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.ana';

-- Dr. Roberto — evento passado (workshop, 1 dia há 5 dias)
INSERT INTO hsg.tb_agenda_medica_excecao (id_medico, dt_inicio, dt_fim, ds_motivo, tp_excecao, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE - INTERVAL '5 days')::date + TIME '08:00',
       (CURRENT_DATE - INTERVAL '4 days')::date + TIME '18:00',
       'Workshop de pediatria neonatal',
       'EVENTO', NOW() - INTERVAL '20 days'
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.roberto';

-- Dra. Fernanda — férias passadas (já encerradas)
INSERT INTO hsg.tb_agenda_medica_excecao (id_medico, dt_inicio, dt_fim, ds_motivo, tp_excecao, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE - INTERVAL '30 days')::timestamp,
       (CURRENT_DATE - INTERVAL '25 days')::timestamp,
       'Recesso de fim de ano',
       'FERIAS', NOW() - INTERVAL '60 days'
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dra.fernanda';

-- Dra. Fernanda — bloqueio futuro (3 dias)
INSERT INTO hsg.tb_agenda_medica_excecao (id_medico, dt_inicio, dt_fim, ds_motivo, tp_excecao, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE + INTERVAL '40 days')::timestamp,
       (CURRENT_DATE + INTERVAL '43 days')::timestamp,
       'Capacitação em neurociência',
       'BLOQUEIO', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dra.fernanda';

-- Dr. Carlos — bloqueio que começa hoje (1 dia)
INSERT INTO hsg.tb_agenda_medica_excecao (id_medico, dt_inicio, dt_fim, ds_motivo, tp_excecao, dt_cadastro)
SELECT m.id_medico,
       CURRENT_DATE + TIME '00:00',
       CURRENT_DATE + TIME '23:59',
       'Cirurgia agendada — sala 3',
       'BLOQUEIO', NOW() - INTERVAL '5 days'
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.carlos';

-- Dr. Carlos — férias longas futuras (10 dias)
INSERT INTO hsg.tb_agenda_medica_excecao (id_medico, dt_inicio, dt_fim, ds_motivo, tp_excecao, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE + INTERVAL '60 days')::timestamp,
       (CURRENT_DATE + INTERVAL '70 days')::timestamp,
       'Férias regulamentares',
       'FERIAS', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.carlos';

-- Dr. João — bloqueio curto retroativo (já encerrado)
INSERT INTO hsg.tb_agenda_medica_excecao (id_medico, dt_inicio, dt_fim, ds_motivo, tp_excecao, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE - INTERVAL '10 days')::date + TIME '14:00',
       (CURRENT_DATE - INTERVAL '10 days')::date + TIME '18:00',
       'Reunião administrativa',
       'BLOQUEIO', NOW() - INTERVAL '12 days'
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.joao';

-- ══════════════════════════════════════════════════════════════════════
-- ── Massa de teste: consultas demo (slots materializados + consultas) ─
-- ══════════════════════════════════════════════════════════════════════
-- Slots inseridos diretamente (RESERVADO) com a consulta vinculada.
-- currval() funciona pois o seed roda numa única sessão após o Flyway.

-- Consulta 1: claudio.filho × dr.joao — PARTICULAR, AGENDADA, daqui a 2 dias 08:00
INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE + INTERVAL '2 days')::date + TIME '08:00',
       (CURRENT_DATE + INTERVAL '2 days')::date + TIME '08:30',
       'RESERVADO', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.joao';

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT (SELECT p.id_pac FROM hsg.tb_pac p
        JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu WHERE c.nm_usu = 'claudio.filho'),
       m.id_medico, m.id_especialidade, currval('hsg.seq_agenda_medica_slot'),
       'PARTICULAR', 'AGENDADA',
       (CURRENT_DATE + INTERVAL '2 days')::date + TIME '08:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.joao';

UPDATE hsg.tb_agenda_medica_slot
SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

-- Consulta 2: mariana.santos × dr.ana — PARTICULAR, CONFIRMADA, daqui a 3 dias 14:00
INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE + INTERVAL '3 days')::date + TIME '14:00',
       (CURRENT_DATE + INTERVAL '3 days')::date + TIME '14:40',
       'RESERVADO', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.ana';

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT (SELECT p.id_pac FROM hsg.tb_pac p
        JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu WHERE c.nm_usu = 'mariana.santos'),
       m.id_medico, m.id_especialidade, currval('hsg.seq_agenda_medica_slot'),
       'PARTICULAR', 'CONFIRMADA',
       (CURRENT_DATE + INTERVAL '3 days')::date + TIME '14:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.ana';

UPDATE hsg.tb_agenda_medica_slot
SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

-- Consulta 3: claudio.filho × dr.roberto — PARTICULAR, REALIZADA, há 7 dias 09:00
INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE - INTERVAL '7 days')::date + TIME '09:00',
       (CURRENT_DATE - INTERVAL '7 days')::date + TIME '09:20',
       'RESERVADO', NOW() - INTERVAL '10 days'
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.roberto';

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT (SELECT p.id_pac FROM hsg.tb_pac p
        JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu WHERE c.nm_usu = 'claudio.filho'),
       m.id_medico, m.id_especialidade, currval('hsg.seq_agenda_medica_slot'),
       'PARTICULAR', 'REALIZADA',
       (CURRENT_DATE - INTERVAL '7 days')::date + TIME '09:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW() - INTERVAL '10 days'
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.roberto';

UPDATE hsg.tb_agenda_medica_slot
SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

-- Consulta 4: claudio.filho × dr.ana — CONVENIO, CONFIRMADA, daqui a 4 dias 14:40
INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE + INTERVAL '4 days')::date + TIME '14:40',
       (CURRENT_DATE + INTERVAL '4 days')::date + TIME '15:20',
       'RESERVADO', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.ana';

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    id_paciente_convenio, tp_atendimento, st_consulta, dt_consulta,
    vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade, currval('hsg.seq_agenda_medica_slot'),
       (SELECT pc.id_pac_conv FROM hsg.tb_pac_conv pc WHERE pc.id_pac = pac.id_pac AND pc.st_pac_conv = 'A'
        ORDER BY pc.id_pac_conv DESC LIMIT 1),
       'CONVENIO', 'CONFIRMADA',
       (CURRENT_DATE + INTERVAL '4 days')::date + TIME '14:40',
       m.nr_valor_consulta,
       ROUND(m.nr_valor_consulta * 0.30, 2),
       ROUND(m.nr_valor_consulta * 0.70, 2),
       NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p
            JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'claudio.filho') pac
WHERE cu.nm_usu = 'dr.ana';

UPDATE hsg.tb_agenda_medica_slot
SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

-- Consulta 5: claudio.filho × dr.carlos — PARTICULAR, CANCELADA, daqui a 5 dias 14:00
INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico,
       (CURRENT_DATE + INTERVAL '5 days')::date + TIME '14:00',
       (CURRENT_DATE + INTERVAL '5 days')::date + TIME '14:30',
       'CANCELADO', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.carlos';

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, dt_cancelamento, ds_cancelamento,
    vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade, currval('hsg.seq_agenda_medica_slot'),
       'PARTICULAR', 'CANCELADA',
       (CURRENT_DATE + INTERVAL '5 days')::date + TIME '14:00',
       NOW(), 'Imprevisto pessoal do paciente.',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p
            JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'claudio.filho') pac
WHERE cu.nm_usu = 'dr.carlos';

UPDATE hsg.tb_agenda_medica_slot
SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

-- ══════════════════════════════════════════════════════════════════════
-- ── Slots LIVRES materializados (próximos 21 dias) p/ busca do paciente ─
-- ══════════════════════════════════════════════════════════════════════
-- Replica a lógica de gerarSlots em SQL: para cada grade ativa, materializa
-- blocos de nr_duracao_min, pulando exceções e slots já existentes.
INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT g.id_medico,
       d.dia::date + g.hr_inicio + (mins || ' minutes')::interval,
       d.dia::date + g.hr_inicio + ((mins + g.nr_duracao_min) || ' minutes')::interval,
       'LIVRE', NOW()
FROM hsg.tb_agenda_medica g
JOIN hsg.tb_medico m ON m.id_medico = g.id_medico AND m.st_medico = 'A'
CROSS JOIN LATERAL generate_series(CURRENT_DATE, CURRENT_DATE + 29, INTERVAL '1 day') AS d(dia)
CROSS JOIN LATERAL generate_series(
        0,
        (EXTRACT(EPOCH FROM (g.hr_fim - g.hr_inicio)) / 60)::int - g.nr_duracao_min,
        g.nr_duracao_min) AS mins
WHERE g.st_ativo = 'A'
  AND EXTRACT(ISODOW FROM d.dia) = g.nr_dia_semana
  AND (d.dia::date + g.hr_inicio + (mins || ' minutes')::interval) > NOW()
  AND NOT EXISTS (
      SELECT 1 FROM hsg.tb_agenda_medica_excecao e
      WHERE e.id_medico = g.id_medico
        AND e.dt_inicio < (d.dia::date + g.hr_inicio + ((mins + g.nr_duracao_min) || ' minutes')::interval)
        AND e.dt_fim    > (d.dia::date + g.hr_inicio + (mins || ' minutes')::interval)
  )
ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

-- ══════════════════════════════════════════════════════════════════════
-- ── Massa de teste: notificações in-app (V28) ─────────────────────────
-- ══════════════════════════════════════════════════════════════════════
-- Distribui notificações entre os perfis para validar a tela.
-- dt_expiracao = dt_criacao + 40 dias.

-- claudio.filho (PACIENTE)
INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'PACIENTE', p.id_pac,
    'Consulta agendada',
    'Sua consulta com Dr(a). João Silva foi marcada para daqui a 2 dias às 08:00.',
    '/paciente/minhas-consultas.xhtml', 'SUCESSO', 'CONSULTA', 'N',
    NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour' + INTERVAL '40 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'PACIENTE', p.id_pac,
    'Convênio aprovado',
    'Sua adesão ao plano foi aprovada. Já está disponível para uso em consultas eletivas.',
    '/paciente/meu-convenio.xhtml', 'SUCESSO', 'CONVENIO', 'S',
    NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days' + INTERVAL '40 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho';

UPDATE hsg.tb_notificacao SET dt_leitura = NOW() - INTERVAL '1 day'
WHERE id_notificacao = currval('hsg.seq_notificacao');

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'PACIENTE', p.id_pac,
    'Lembrete: consulta amanhã',
    'Você tem uma consulta agendada para amanhã. Chegue 15 minutos antes para conferência de carteirinha.',
    '/paciente/minhas-consultas.xhtml', 'INFO', 'CONSULTA', 'N',
    NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours' + INTERVAL '40 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'PACIENTE', p.id_pac,
    'Consulta marcada como falta',
    'Sua consulta com Dr(a). Roberto Mendes do dia 7 dias atrás foi marcada como falta. Em caso de dúvida procure a recepção.',
    '/paciente/minhas-consultas.xhtml', 'INFO', 'CONSULTA', 'N',
    NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days' + INTERVAL '40 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'claudio.filho';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'PACIENTE', p.id_pac,
    'Convênio em carência',
    'Você possui procedimentos do plano em período de carência. Consulte detalhes na tela do seu convênio.',
    '/paciente/meu-convenio.xhtml', 'ALERTA', 'CONVENIO', 'N',
    NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days' + INTERVAL '40 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'mariana.santos';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'PACIENTE', p.id_pac,
    'Consulta agendada',
    'Sua consulta com Dra. Ana Carvalho foi marcada para daqui a 3 dias às 14:00.',
    '/paciente/minhas-consultas.xhtml', 'SUCESSO', 'CONSULTA', 'N',
    NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours' + INTERVAL '40 days'
FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
WHERE c.nm_usu = 'mariana.santos';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'MEDICO', m.id_medico,
    'Nova consulta agendada',
    'Paciente Cláudio Filho marcou consulta para daqui a 2 dias às 08:00.',
    '/clinica/notificacoes.xhtml', 'INFO', 'CONSULTA', 'N',
    NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour' + INTERVAL '40 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu c ON c.id_conta_usu = m.id_conta_usu_medico
WHERE c.nm_usu = 'dr.joao';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'MEDICO', m.id_medico,
    'Consulta cancelada pelo paciente',
    'Paciente Mariana Santos cancelou a consulta de 5 dias atrás às 14:00. Motivo: imprevisto pessoal.',
    '/clinica/notificacoes.xhtml', 'ALERTA', 'CONSULTA', 'N',
    NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days' + INTERVAL '40 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu c ON c.id_conta_usu = m.id_conta_usu_medico
WHERE c.nm_usu = 'dr.joao';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'MEDICO', m.id_medico,
    'Faixa de exceção registrada',
    'Sua exceção de FÉRIAS programada (daqui a 15 dias) foi registrada pela administração.',
    NULL, 'INFO', 'AGENDA', 'S',
    NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days' + INTERVAL '40 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu c ON c.id_conta_usu = m.id_conta_usu_medico
WHERE c.nm_usu = 'dr.joao';

UPDATE hsg.tb_notificacao SET dt_leitura = NOW() - INTERVAL '5 days'
WHERE id_notificacao = currval('hsg.seq_notificacao');

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'MEDICO', m.id_medico,
    'Manutenção programada do sistema',
    'O HSG HIS terá manutenção neste sábado, das 02:00 às 04:00. Durante esse período o portal ficará indisponível.',
    NULL, 'INFO', 'SISTEMA', 'N',
    NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours' + INTERVAL '40 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu c ON c.id_conta_usu = m.id_conta_usu_medico
WHERE c.nm_usu = 'dr.joao';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'MEDICO', m.id_medico,
    'Consulta marcada como falta',
    'A consulta com Mariana Santos do dia 10 dias atrás foi marcada como falta pelo sistema. Ajuste manualmente se foi realizada.',
    '/clinica/notificacoes.xhtml', 'ALERTA', 'CONSULTA', 'N',
    NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days' + INTERVAL '40 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu c ON c.id_conta_usu = m.id_conta_usu_medico
WHERE c.nm_usu = 'dr.ana';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'MEDICO', m.id_medico,
    'Nova consulta agendada',
    'Paciente Mariana Santos marcou consulta para daqui a 3 dias às 14:00.',
    '/clinica/notificacoes.xhtml', 'INFO', 'CONSULTA', 'N',
    NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours' + INTERVAL '40 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu c ON c.id_conta_usu = m.id_conta_usu_medico
WHERE c.nm_usu = 'dr.ana';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'ENFERMEIRO', e.id_enfer,
    'Treinamento obrigatório',
    'Treinamento de protocolos de UTI agendado para próxima semana. Acesse o portal de capacitação para confirmar presença.',
    NULL, 'ALERTA', 'SISTEMA', 'N',
    NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day' + INTERVAL '40 days'
FROM hsg.tb_enfer e JOIN hsg.tb_conta_usu c ON c.id_conta_usu = e.id_conta_usu_enfer
WHERE c.nm_usu = 'enf.lucas';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'ENFERMEIRO', e.id_enfer,
    'Boas-vindas ao HSG HIS',
    'Seu acesso ao portal foi liberado. Em caso de dúvidas procure a coordenação.',
    NULL, 'INFO', 'SISTEMA', 'S',
    NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days' + INTERVAL '40 days'
FROM hsg.tb_enfer e JOIN hsg.tb_conta_usu c ON c.id_conta_usu = e.id_conta_usu_enfer
WHERE c.nm_usu = 'enf.lucas';

UPDATE hsg.tb_notificacao SET dt_leitura = NOW() - INTERVAL '7 days'
WHERE id_notificacao = currval('hsg.seq_notificacao');

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'ADMIN', a.id_adm,
    'Nova solicitação de convênio',
    'Paciente Mariana Santos solicitou adesão ao plano Premium. Avaliar dados da carteirinha e aprovar/rejeitar.',
    '/admin/aprovacao-convenios.xhtml', 'INFO', 'CONVENIO', 'N',
    NOW() - INTERVAL '30 minutes', NOW() - INTERVAL '30 minutes' + INTERVAL '40 days'
FROM hsg.tb_adm a JOIN hsg.tb_conta_usu c ON c.id_conta_usu = a.id_conta_usu_adm
WHERE c.nm_usu = 'admin.hsg';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'ADMIN', a.id_adm,
    'Pré-cadastros profissionais pendentes',
    '3 pré-cadastros aguardam aprovação. Acesse a tela de pré-cadastro para revisar.',
    '/admin/pre-cadastro-profissional.xhtml', 'ALERTA', 'SISTEMA', 'N',
    NOW() - INTERVAL '6 hours', NOW() - INTERVAL '6 hours' + INTERVAL '40 days'
FROM hsg.tb_adm a JOIN hsg.tb_conta_usu c ON c.id_conta_usu = a.id_conta_usu_adm
WHERE c.nm_usu = 'admin.hsg';

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'ADMIN', a.id_adm,
    'Geração de slots em massa concluída',
    '342 slot(s) gerado(s) para 5 médico(s) — próximos 30 dias.',
    '/admin/agenda-medica.xhtml', 'SUCESSO', 'AGENDA', 'S',
    NOW() - INTERVAL '15 hours', NOW() - INTERVAL '15 hours' + INTERVAL '40 days'
FROM hsg.tb_adm a JOIN hsg.tb_conta_usu c ON c.id_conta_usu = a.id_conta_usu_adm
WHERE c.nm_usu = 'admin.hsg';

UPDATE hsg.tb_notificacao SET dt_leitura = NOW() - INTERVAL '14 hours'
WHERE id_notificacao = currval('hsg.seq_notificacao');

INSERT INTO hsg.tb_notificacao (tp_destinatario, id_destinatario, ds_titulo, ds_mensagem,
    ds_link, tp_notificacao, tp_categoria, fl_lida, dt_criacao, dt_expiracao)
SELECT 'ADMIN', a.id_adm,
    'Falha no envio de e-mail',
    'Envio de e-mail de aprovação de convênio falhou para 1 paciente nas últimas 24h. A aprovação foi mantida; verifique logs do MailService.',
    NULL, 'ERRO', 'SISTEMA', 'N',
    NOW() - INTERVAL '20 hours', NOW() - INTERVAL '20 hours' + INTERVAL '40 days'
FROM hsg.tb_adm a JOIN hsg.tb_conta_usu c ON c.id_conta_usu = a.id_conta_usu_adm
WHERE c.nm_usu = 'admin.hsg';

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, CURRENT_DATE + TIME '09:00', CURRENT_DATE + TIME '09:30',
       'RESERVADO', NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.joao' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = CURRENT_DATE + TIME '09:00'),
       'PARTICULAR', 'CONFIRMADA', CURRENT_DATE + TIME '09:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'claudio.filho') pac
WHERE cu.nm_usu = 'dr.joao' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, CURRENT_DATE + TIME '10:00', CURRENT_DATE + TIME '10:30',
       'RESERVADO', NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.joao' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = CURRENT_DATE + TIME '10:00'),
       'PARTICULAR', 'AGENDADA', CURRENT_DATE + TIME '10:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'mariana.santos') pac
WHERE cu.nm_usu = 'dr.joao' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, (CURRENT_DATE + 1) + TIME '14:00', (CURRENT_DATE + 1) + TIME '14:40',
       'RESERVADO', NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.ana' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = (CURRENT_DATE + 1) + TIME '14:00'),
       'PARTICULAR', 'AGENDADA', (CURRENT_DATE + 1) + TIME '14:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'pedro.oliveira') pac
WHERE cu.nm_usu = 'dr.ana' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, (CURRENT_DATE - 2) + TIME '09:20', (CURRENT_DATE - 2) + TIME '09:40',
       'RESERVADO', NOW() - INTERVAL '4 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.roberto' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio,
    ds_observacao_clinica, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = (CURRENT_DATE - 2) + TIME '09:20'),
       'PARTICULAR', 'REALIZADA', (CURRENT_DATE - 2) + TIME '09:20',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00,
       'Paciente apresenta quadro leve de gastroenterite, recomendado hidratação e dieta branda por 3 dias.',
       NOW() - INTERVAL '4 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'carla.silva') pac
WHERE cu.nm_usu = 'dr.roberto' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, (CURRENT_DATE - 1) + TIME '09:00', (CURRENT_DATE - 1) + TIME '09:45',
       'RESERVADO', NOW() - INTERVAL '3 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dra.fernanda' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = (CURRENT_DATE - 1) + TIME '09:00'),
       'PARTICULAR', 'FALTOU', (CURRENT_DATE - 1) + TIME '09:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW() - INTERVAL '3 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'joao.lima') pac
WHERE cu.nm_usu = 'dra.fernanda' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, (CURRENT_DATE + 2) + TIME '14:00', (CURRENT_DATE + 2) + TIME '14:30',
       'RESERVADO', NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.carlos' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = (CURRENT_DATE + 2) + TIME '14:00'),
       'PARTICULAR', 'AGENDADA', (CURRENT_DATE + 2) + TIME '14:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'beatriz.costa') pac
WHERE cu.nm_usu = 'dr.carlos' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, (CURRENT_DATE - 3) + TIME '10:30', (CURRENT_DATE - 3) + TIME '11:15',
       'RESERVADO', NOW() - INTERVAL '5 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dra.fernanda' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio,
    ds_observacao_clinica, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = (CURRENT_DATE - 3) + TIME '10:30'),
       'PARTICULAR', 'REALIZADA', (CURRENT_DATE - 3) + TIME '10:30',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00,
       'Cefaleia tensional. Encaminhado para neurologia ambulatorial. Prescrito analgésico SOS.',
       NOW() - INTERVAL '5 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'mariana.santos') pac
WHERE cu.nm_usu = 'dra.fernanda' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, CURRENT_DATE + TIME '11:00', CURRENT_DATE + TIME '11:30',
       'CANCELADO', NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.joao' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, dt_cancelamento, ds_cancelamento,
    vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = CURRENT_DATE + TIME '11:00'),
       'PARTICULAR', 'CANCELADA', CURRENT_DATE + TIME '11:00',
       NOW(), 'Cancelado pela clínica - emergência hospitalar.',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW() - INTERVAL '1 day'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'pedro.oliveira') pac
WHERE cu.nm_usu = 'dr.joao' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, CURRENT_DATE + TIME '15:00', CURRENT_DATE + TIME '15:40',
       'RESERVADO', NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.ana' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = CURRENT_DATE + TIME '15:00'),
       'PARTICULAR', 'CONFIRMADA', CURRENT_DATE + TIME '15:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00, NOW()
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'carla.silva') pac
WHERE cu.nm_usu = 'dr.ana' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_agenda_medica_slot (id_medico, dt_inicio, dt_fim, st_slot, dt_cadastro)
SELECT m.id_medico, (CURRENT_DATE - 1) + TIME '09:00', (CURRENT_DATE - 1) + TIME '09:20',
       'RESERVADO', NOW() - INTERVAL '3 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
WHERE cu.nm_usu = 'dr.roberto' ON CONFLICT (id_medico, dt_inicio) DO NOTHING;

INSERT INTO hsg.tb_consulta (id_paciente, id_medico, id_especialidade, id_agenda_slot,
    tp_atendimento, st_consulta, dt_consulta, vl_consulta, vl_copagamento, vl_cobertura_convenio,
    ds_observacao_clinica, dt_cadastro)
SELECT pac.id_pac, m.id_medico, m.id_especialidade,
       (SELECT id_agenda_slot FROM hsg.tb_agenda_medica_slot
        WHERE id_medico = m.id_medico AND dt_inicio = (CURRENT_DATE - 1) + TIME '09:00'),
       'PARTICULAR', 'REALIZADA', (CURRENT_DATE - 1) + TIME '09:00',
       m.nr_valor_consulta, m.nr_valor_consulta, 0.00,
       'Acompanhamento pediátrico de rotina. Curva de crescimento dentro do esperado. Vacinação em dia.',
       NOW() - INTERVAL '3 days'
FROM hsg.tb_medico m JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (SELECT p.id_pac FROM hsg.tb_pac p JOIN hsg.tb_conta_usu c ON c.id_conta_usu = p.id_conta_usu
            WHERE c.nm_usu = 'beatriz.costa') pac
WHERE cu.nm_usu = 'dr.roberto' ON CONFLICT (id_agenda_slot) DO NOTHING;

UPDATE hsg.tb_agenda_medica_slot SET id_consulta = currval('hsg.seq_consulta')
WHERE id_agenda_slot = currval('hsg.seq_agenda_medica_slot');

INSERT INTO hsg.tb_consulta_historico (id_consulta, tp_acao, id_responsavel, tp_responsavel,
    ds_observacao, dt_acao)
SELECT c.id_consulta, 'AGENDADA', c.id_paciente, 'PACIENTE',
       'Consulta marcada pelo paciente via portal.',
       c.dt_cadastro
FROM hsg.tb_consulta c;

INSERT INTO hsg.tb_consulta_historico (id_consulta, tp_acao, id_responsavel, tp_responsavel,
    ds_observacao, dt_acao)
SELECT c.id_consulta, 'CHECK_IN',
       (SELECT e.id_enfer FROM hsg.tb_enfer e
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = e.id_conta_usu_enfer
        WHERE cu.nm_usu = 'enf.maria'),
       'ENFERMEIRO',
       'Paciente compareceu à recepção e foi triado.',
       c.dt_consulta - INTERVAL '15 minutes'
FROM hsg.tb_consulta c
WHERE c.st_consulta IN ('CONFIRMADA','REALIZADA');

INSERT INTO hsg.tb_consulta_historico (id_consulta, tp_acao, id_responsavel, tp_responsavel,
    ds_observacao, dt_acao)
SELECT c.id_consulta, 'REALIZADA', c.id_medico, 'MEDICO',
       'Atendimento concluído com observação clínica registrada.',
       c.dt_consulta + INTERVAL '30 minutes'
FROM hsg.tb_consulta c
WHERE c.st_consulta = 'REALIZADA';

INSERT INTO hsg.tb_consulta_historico (id_consulta, tp_acao, id_responsavel, tp_responsavel,
    ds_observacao, dt_acao)
SELECT c.id_consulta, 'FALTOU', NULL, 'SISTEMA',
       'Auto-falta registrada — paciente não confirmou chegada após 30 min do horário.',
       c.dt_consulta + INTERVAL '30 minutes'
FROM hsg.tb_consulta c
WHERE c.st_consulta = 'FALTOU';

INSERT INTO hsg.tb_consulta_historico (id_consulta, tp_acao, id_responsavel, tp_responsavel,
    ds_observacao, dt_acao)
SELECT c.id_consulta, 'CANCELADA', c.id_medico, 'MEDICO',
       COALESCE(c.ds_cancelamento, 'Cancelada pela clínica.'),
       c.dt_cancelamento
FROM hsg.tb_consulta c
WHERE c.st_consulta = 'CANCELADA'
  AND c.ds_cancelamento ILIKE '%clínica%';

INSERT INTO hsg.tb_consulta_historico (id_consulta, tp_acao, id_responsavel, tp_responsavel,
    ds_observacao, dt_acao)
SELECT c.id_consulta, 'CANCELADA', c.id_paciente, 'PACIENTE',
       COALESCE(c.ds_cancelamento, 'Cancelada pelo paciente.'),
       c.dt_cancelamento
FROM hsg.tb_consulta c
WHERE c.st_consulta = 'CANCELADA'
  AND c.ds_cancelamento NOT ILIKE '%clínica%';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Triagem inicial',
       'PA 130/85 mmHg, FC 78 bpm, T 36,8°C. Paciente queixa-se de dor abdominal leve há 2 dias.',
       (SELECT e.id_enfer FROM hsg.tb_enfer e
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = e.id_conta_usu_enfer
        WHERE cu.nm_usu = 'enf.maria'),
       'ENFERMEIRO',
       c.dt_consulta - INTERVAL '10 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'claudio.filho'
  AND c.dt_consulta = (CURRENT_DATE - 7) + TIME '09:00';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Conduta clínica',
       'Exame físico sem sinais de irritação peritoneal. Solicitado hemograma e USG abdominal. Retorno em 7 dias.',
       c.id_medico, 'MEDICO',
       c.dt_consulta + INTERVAL '25 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'claudio.filho'
  AND c.dt_consulta = (CURRENT_DATE - 7) + TIME '09:00';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Sinais vitais',
       'PA 110/70 mmHg, FC 88 bpm, T 37,6°C. Refere náuseas e fezes amolecidas desde ontem.',
       (SELECT e.id_enfer FROM hsg.tb_enfer e
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = e.id_conta_usu_enfer
        WHERE cu.nm_usu = 'enf.lucas'),
       'ENFERMEIRO',
       c.dt_consulta - INTERVAL '8 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'carla.silva'
  AND c.dt_consulta = (CURRENT_DATE - 2) + TIME '09:20';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Anamnese',
       'Quadro compatível com gastroenterite aguda viral. Sem desidratação grave. Tolera líquidos por VO.',
       c.id_medico, 'MEDICO', c.dt_consulta + INTERVAL '10 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'carla.silva'
  AND c.dt_consulta = (CURRENT_DATE - 2) + TIME '09:20';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Plano terapêutico',
       'Hidratação oral, dieta branda por 3 dias, sintomáticos SOS. Retorno se febre > 38,5°C ou sangue nas fezes.',
       c.id_medico, 'MEDICO', c.dt_consulta + INTERVAL '20 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'carla.silva'
  AND c.dt_consulta = (CURRENT_DATE - 2) + TIME '09:20';

-- ▸ Consulta 12 (dra.fernanda × mariana.santos — REALIZADA há 3d)
INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Triagem',
       'PA 125/80 mmHg, FC 72 bpm. Refere cefaleia bilateral em peso há 5 dias, pior no final do expediente.',
       (SELECT e.id_enfer FROM hsg.tb_enfer e
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = e.id_conta_usu_enfer
        WHERE cu.nm_usu = 'enf.juliana'),
       'ENFERMEIRO',
       c.dt_consulta - INTERVAL '12 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dra.fernanda' AND cp.nm_usu = 'mariana.santos'
  AND c.dt_consulta = (CURRENT_DATE - 3) + TIME '10:30';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Exame neurológico',
       'Glasgow 15. Pupilas isocóricas e fotorreagentes. Sem déficit motor ou sensitivo. Força muscular grau V/V.',
       c.id_medico, 'MEDICO', c.dt_consulta + INTERVAL '15 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dra.fernanda' AND cp.nm_usu = 'mariana.santos'
  AND c.dt_consulta = (CURRENT_DATE - 3) + TIME '10:30';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Encaminhamento',
       'Encaminhado para neurologia ambulatorial para avaliação de cefaleia tensional crônica. Prescrito amitriptilina 25 mg à noite.',
       c.id_medico, 'MEDICO', c.dt_consulta + INTERVAL '25 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dra.fernanda' AND cp.nm_usu = 'mariana.santos'
  AND c.dt_consulta = (CURRENT_DATE - 3) + TIME '10:30';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Triagem pediátrica',
       'Peso 18,3 kg, altura 110 cm, FC 96 bpm, T 36,5°C. Mãe relata desenvolvimento sem queixas.',
       (SELECT e.id_enfer FROM hsg.tb_enfer e
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = e.id_conta_usu_enfer
        WHERE cu.nm_usu = 'enf.camila'),
       'ENFERMEIRO',
       c.dt_consulta - INTERVAL '10 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'beatriz.costa'
  AND c.dt_consulta = (CURRENT_DATE - 1) + TIME '09:00';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Acompanhamento',
       'Curva de crescimento em P50. Marcos de desenvolvimento adequados para idade. Caderneta vacinal em dia.',
       c.id_medico, 'MEDICO', c.dt_consulta + INTERVAL '15 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'beatriz.costa'
  AND c.dt_consulta = (CURRENT_DATE - 1) + TIME '09:00';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Triagem',
       'PA 118/76 mmHg, FC 70 bpm, T 36,4°C. Sem queixas no momento. Aguardando atendimento médico.',
       (SELECT e.id_enfer FROM hsg.tb_enfer e
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = e.id_conta_usu_enfer
        WHERE cu.nm_usu = 'enf.maria'),
       'ENFERMEIRO',
       c.dt_consulta - INTERVAL '10 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.joao' AND cp.nm_usu = 'claudio.filho'
  AND c.dt_consulta = CURRENT_DATE + TIME '09:00';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Triagem',
       'PA 122/78 mmHg, FC 74 bpm. Refere ansiedade leve antes do atendimento.',
       (SELECT e.id_enfer FROM hsg.tb_enfer e
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = e.id_conta_usu_enfer
        WHERE cu.nm_usu = 'enf.juliana'),
       'ENFERMEIRO',
       c.dt_consulta - INTERVAL '15 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.ana' AND cp.nm_usu = 'carla.silva'
  AND c.dt_consulta = CURRENT_DATE + TIME '15:00';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Observação administrativa',
       'Carteirinha de convênio validada na recepção. Autorização prévia não necessária para a especialidade.',
       (SELECT a.id_adm FROM hsg.tb_adm a
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = a.id_conta_usu_adm
        WHERE cu.nm_usu = 'admin.hsg'),
       'ADMIN',
       c.dt_consulta - INTERVAL '20 minutes'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.ana' AND cp.nm_usu = 'carla.silva'
  AND c.dt_consulta = CURRENT_DATE + TIME '15:00';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Confirmação telefônica',
       'Paciente confirmou presença por telefone. Solicitou trazer exames laboratoriais recentes.',
       (SELECT a.id_adm FROM hsg.tb_adm a
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = a.id_conta_usu_adm
        WHERE cu.nm_usu = 'admin.hsg'),
       'ADMIN',
       NOW() - INTERVAL '6 hours'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.ana' AND cp.nm_usu = 'mariana.santos'
  AND c.dt_consulta = (CURRENT_DATE + 3) + TIME '14:00';

INSERT INTO hsg.tb_consulta_anotacao (id_consulta, ds_titulo, ds_descricao,
    id_responsavel, tp_responsavel, dt_criacao)
SELECT c.id_consulta,
       'Convênio validado',
       'Cobertura ativa confirmada com a operadora. Copagamento de 30% aplicável ao paciente.',
       (SELECT a.id_adm FROM hsg.tb_adm a
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = a.id_conta_usu_adm
        WHERE cu.nm_usu = 'admin.hsg'),
       'ADMIN',
       NOW() - INTERVAL '2 hours'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.ana' AND cp.nm_usu = 'claudio.filho'
  AND c.dt_consulta = (CURRENT_DATE + 4) + TIME '14:40';

INSERT INTO hsg.tb_arquivo (ds_path_logico, ds_dominio, ds_nome_original, ds_content_type,
    nr_tamanho_bytes, ds_sha256, id_consulta, id_responsavel, tp_responsavel, dt_upload, st_arquivo)
SELECT '/anexos/consulta/' || c.id_consulta || '/'
       || TO_CHAR(c.dt_consulta, 'YYYY/MM') || '/seed-receita-c3.pdf',
       'ANEXO_CONSULTA', 'receita_dipirona.pdf', 'application/pdf',
       124853,
       '7d3c8b1f9a2e6c1c4dbe5f7a3c2b0d9e8f1a6b5c4d3e2f1a0b9c8d7e6f5a4b3c',
       c.id_consulta, c.id_medico, 'MEDICO',
       c.dt_consulta + INTERVAL '20 minutes', 'A'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'claudio.filho'
  AND c.dt_consulta = (CURRENT_DATE - 7) + TIME '09:00';

INSERT INTO hsg.tb_arquivo (ds_path_logico, ds_dominio, ds_nome_original, ds_content_type,
    nr_tamanho_bytes, ds_sha256, id_consulta, id_responsavel, tp_responsavel, dt_upload, st_arquivo)
SELECT '/exames/consulta/' || c.id_consulta || '/'
       || TO_CHAR(c.dt_consulta - INTERVAL '1 day', 'YYYY/MM') || '/seed-hemograma-c9.pdf',
       'EXAME_CONSULTA', 'hemograma_completo.pdf', 'application/pdf',
       287901,
       'a1b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef123456',
       c.id_consulta, c.id_paciente, 'PACIENTE',
       c.dt_consulta - INTERVAL '1 day', 'A'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'carla.silva'
  AND c.dt_consulta = (CURRENT_DATE - 2) + TIME '09:20';

INSERT INTO hsg.tb_arquivo (ds_path_logico, ds_dominio, ds_nome_original, ds_content_type,
    nr_tamanho_bytes, ds_sha256, id_anotacao, id_responsavel, tp_responsavel, dt_upload, st_arquivo)
SELECT '/anexos/anotacao/' || a.id_consulta_anotacao || '/'
       || TO_CHAR(a.dt_criacao, 'YYYY/MM') || '/seed-prescricao-an.png',
       'ANEXO_ANOTACAO', 'prescricao_assinada.png', 'image/png',
       89124,
       'bbbb1111cccc2222dddd3333eeee4444ffff5555aaaa6666bbbb7777cccc8888',
       a.id_consulta_anotacao, c.id_medico, 'MEDICO',
       a.dt_criacao + INTERVAL '5 minutes', 'A'
FROM hsg.tb_consulta_anotacao a
JOIN hsg.tb_consulta c ON c.id_consulta = a.id_consulta
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'carla.silva'
  AND a.ds_titulo = 'Plano terapêutico'
  AND c.dt_consulta = (CURRENT_DATE - 2) + TIME '09:20';

INSERT INTO hsg.tb_arquivo (ds_path_logico, ds_dominio, ds_nome_original, ds_content_type,
    nr_tamanho_bytes, ds_sha256, id_consulta, id_responsavel, tp_responsavel, dt_upload, st_arquivo)
SELECT '/anexos/consulta/' || c.id_consulta || '/'
       || TO_CHAR(c.dt_consulta, 'YYYY/MM') || '/seed-encaminhamento-c12.pdf',
       'ANEXO_CONSULTA', 'encaminhamento_neuro.pdf', 'application/pdf',
       156432,
       'f1e2d3c4b5a6978869574635241302f1e2d3c4b5a6978869574635241302f1e2',
       c.id_consulta, c.id_medico, 'MEDICO',
       c.dt_consulta + INTERVAL '30 minutes', 'A'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dra.fernanda' AND cp.nm_usu = 'mariana.santos'
  AND c.dt_consulta = (CURRENT_DATE - 3) + TIME '10:30';

INSERT INTO hsg.tb_arquivo (ds_path_logico, ds_dominio, ds_nome_original, ds_content_type,
    nr_tamanho_bytes, ds_sha256, id_consulta, id_responsavel, tp_responsavel, dt_upload, st_arquivo)
SELECT '/exames/consulta/' || c.id_consulta || '/'
       || TO_CHAR(NOW() - INTERVAL '1 day', 'YYYY/MM') || '/seed-tomografia-c14.pdf',
       'EXAME_CONSULTA', 'tomografia_torax.pdf', 'application/pdf',
       1245876,
       '00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff',
       c.id_consulta, c.id_paciente, 'PACIENTE',
       NOW() - INTERVAL '1 day', 'A'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.ana' AND cp.nm_usu = 'carla.silva'
  AND c.dt_consulta = CURRENT_DATE + TIME '15:00';

INSERT INTO hsg.tb_arquivo (ds_path_logico, ds_dominio, ds_nome_original, ds_content_type,
    nr_tamanho_bytes, ds_sha256, id_consulta, id_responsavel, tp_responsavel, dt_upload, st_arquivo)
SELECT '/exames/consulta/' || c.id_consulta || '/'
       || TO_CHAR(NOW() - INTERVAL '3 hours', 'YYYY/MM') || '/seed-rx-c6.jpg',
       'EXAME_CONSULTA', 'radiografia_torax.jpg', 'image/jpeg',
       452301,
       '99aabbccdd0011223344556677889900aabbccddee1122334455667788990011',
       c.id_consulta, c.id_paciente, 'PACIENTE',
       NOW() - INTERVAL '3 hours', 'A'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.joao' AND cp.nm_usu = 'claudio.filho'
  AND c.dt_consulta = CURRENT_DATE + TIME '09:00';

INSERT INTO hsg.tb_arquivo (ds_path_logico, ds_dominio, ds_nome_original, ds_content_type,
    nr_tamanho_bytes, ds_sha256, id_consulta, id_responsavel, tp_responsavel, dt_upload, st_arquivo)
SELECT '/anexos/consulta/' || c.id_consulta || '/'
       || TO_CHAR(c.dt_consulta, 'YYYY/MM') || '/seed-atestado-c15.pdf',
       'ANEXO_CONSULTA', 'atestado_acompanhamento.pdf', 'application/pdf',
       67234,
       'cafe0babe1234567890fedcba9876543210cafe0babe1234567890fedcba9876',
       c.id_consulta,
       (SELECT e.id_enfer FROM hsg.tb_enfer e
        JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = e.id_conta_usu_enfer
        WHERE cu.nm_usu = 'enf.camila'),
       'ENFERMEIRO',
       c.dt_consulta + INTERVAL '40 minutes', 'A'
FROM hsg.tb_consulta c
JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
JOIN hsg.tb_conta_usu cp ON cp.id_conta_usu = p.id_conta_usu
WHERE cu.nm_usu = 'dr.roberto' AND cp.nm_usu = 'beatriz.costa'
  AND c.dt_consulta = (CURRENT_DATE - 1) + TIME '09:00';

-- ══════════════════════════════════════════════════════════════════════
-- ── Massa: receituário (V33) ──────────────────────────────────────────
-- ══════════════════════════════════════════════════════════════════════
-- 3 receitas ATIVAS em consultas REALIZADAS, cada uma com 1-2 itens.

DO $$
DECLARE
    v_id_consulta BIGINT;
    v_id_medico   BIGINT;
    v_id_receita  BIGINT;
BEGIN
    -- Receita 1 — C3 (dr.roberto × claudio.filho, REALIZADA -7d): 2 itens
    SELECT c.id_consulta, c.id_medico INTO v_id_consulta, v_id_medico
    FROM hsg.tb_consulta c
    JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
    JOIN hsg.tb_conta_usu cum ON cum.id_conta_usu = m.id_conta_usu_medico
    JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
    JOIN hsg.tb_conta_usu cup ON cup.id_conta_usu = p.id_conta_usu
    WHERE cum.nm_usu = 'dr.roberto' AND cup.nm_usu = 'claudio.filho'
      AND c.dt_consulta = (CURRENT_DATE - 7) + TIME '09:00';

    IF v_id_consulta IS NOT NULL THEN
        INSERT INTO hsg.tb_receita (id_consulta, id_medico, dt_emissao, st_receita)
        VALUES (v_id_consulta, v_id_medico,
                (CURRENT_DATE - 7) + TIME '09:25', 'A')
        RETURNING id_receita INTO v_id_receita;

        INSERT INTO hsg.tb_receita_item (id_receita, ds_medicamento, ds_posologia, ds_observacao, ds_cid_10, nr_ordem)
        VALUES (v_id_receita, 'Dipirona sódica 500mg',
                '1 comprimido via oral de 6/6 horas',
                'Suspender em caso de melhora completa dos sintomas.',
                'R10', 1);
        INSERT INTO hsg.tb_receita_item (id_receita, ds_medicamento, ds_posologia, ds_observacao, ds_cid_10, nr_ordem)
        VALUES (v_id_receita, 'Soro de reidratação oral',
                '1 sachê dissolvido em 1 litro de água, ingerir conforme sede',
                NULL, NULL, 2);
    END IF;

    -- Receita 2 — C9 (dr.roberto × carla.silva, REALIZADA -2d): 1 item
    SELECT c.id_consulta, c.id_medico INTO v_id_consulta, v_id_medico
    FROM hsg.tb_consulta c
    JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
    JOIN hsg.tb_conta_usu cum ON cum.id_conta_usu = m.id_conta_usu_medico
    JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
    JOIN hsg.tb_conta_usu cup ON cup.id_conta_usu = p.id_conta_usu
    WHERE cum.nm_usu = 'dr.roberto' AND cup.nm_usu = 'carla.silva'
      AND c.dt_consulta = (CURRENT_DATE - 2) + TIME '09:20';

    IF v_id_consulta IS NOT NULL THEN
        INSERT INTO hsg.tb_receita (id_consulta, id_medico, dt_emissao, st_receita)
        VALUES (v_id_consulta, v_id_medico,
                (CURRENT_DATE - 2) + TIME '09:55', 'A')
        RETURNING id_receita INTO v_id_receita;

        INSERT INTO hsg.tb_receita_item (id_receita, ds_medicamento, ds_posologia, ds_observacao, ds_cid_10, nr_ordem)
        VALUES (v_id_receita, 'Sais para reidratação oral (SRO)',
                '1 sachê após cada evacuação líquida, até 8 sachês/dia',
                'Manter hidratação por 48-72h. Retornar se sangue nas fezes ou febre alta.',
                'A09', 1);
    END IF;

    -- Receita 3 — C12 (dra.fernanda × mariana.santos, REALIZADA -3d): 2 itens
    SELECT c.id_consulta, c.id_medico INTO v_id_consulta, v_id_medico
    FROM hsg.tb_consulta c
    JOIN hsg.tb_medico m ON m.id_medico = c.id_medico
    JOIN hsg.tb_conta_usu cum ON cum.id_conta_usu = m.id_conta_usu_medico
    JOIN hsg.tb_pac p ON p.id_pac = c.id_paciente
    JOIN hsg.tb_conta_usu cup ON cup.id_conta_usu = p.id_conta_usu
    WHERE cum.nm_usu = 'dra.fernanda' AND cup.nm_usu = 'mariana.santos'
      AND c.dt_consulta = (CURRENT_DATE - 3) + TIME '10:30';

    IF v_id_consulta IS NOT NULL THEN
        INSERT INTO hsg.tb_receita (id_consulta, id_medico, dt_emissao, st_receita)
        VALUES (v_id_consulta, v_id_medico,
                (CURRENT_DATE - 3) + TIME '11:00', 'A')
        RETURNING id_receita INTO v_id_receita;

        INSERT INTO hsg.tb_receita_item (id_receita, ds_medicamento, ds_posologia, ds_observacao, ds_cid_10, nr_ordem)
        VALUES (v_id_receita, 'Amitriptilina 25mg',
                '1 comprimido via oral, à noite, por 30 dias',
                'Avaliar resposta em 4 semanas. Pode causar sonolência inicial.',
                'G44', 1);
        INSERT INTO hsg.tb_receita_item (id_receita, ds_medicamento, ds_posologia, ds_observacao, ds_cid_10, nr_ordem)
        VALUES (v_id_receita, 'Paracetamol 750mg',
                '1 comprimido via oral SOS em caso de dor (máx. 4/dia)',
                NULL, 'R51', 2);
    END IF;
END $$;
