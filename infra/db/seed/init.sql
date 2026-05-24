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

-- ══════════════════════════════════════════════════════════════════════
-- ── Massa de teste: agenda médica (grades demo) ───────────────────────
-- ══════════════════════════════════════════════════════════════════════

-- Atualiza duração padrão de consulta dos médicos demo
UPDATE hsg.tb_medico SET nr_duracao_consulta_min = 30
WHERE nr_duracao_consulta_min IS NULL;

-- Dr. João Silva (Clínica Médica) — Seg/Qua/Sex 08:00-12:00, slot 30min
INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, dow, TIME '08:00', TIME '12:00', 30, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (VALUES (1), (3), (5)) AS d(dow)
WHERE cu.nm_usu = 'dr.joao';

-- Dra. Ana Carvalho (Cardiologia) — Ter/Qui 14:00-18:00, slot 40min
INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, dow, TIME '14:00', TIME '18:00', 40, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (VALUES (2), (4)) AS d(dow)
WHERE cu.nm_usu = 'dr.ana';

-- Dr. Roberto Mendes (Pediatria) — Seg-Sex 09:00-11:00, slot 20min
INSERT INTO hsg.tb_agenda_medica (id_medico, nr_dia_semana, hr_inicio, hr_fim, nr_duracao_min, st_ativo, dt_cadastro)
SELECT m.id_medico, dow, TIME '09:00', TIME '11:00', 20, 'A', NOW()
FROM hsg.tb_medico m
JOIN hsg.tb_conta_usu cu ON cu.id_conta_usu = m.id_conta_usu_medico
CROSS JOIN (VALUES (1), (2), (3), (4), (5)) AS d(dow)
WHERE cu.nm_usu = 'dr.roberto';
