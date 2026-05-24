INSERT INTO hsg.tb_reg_cob (
    id_pl_conv, ds_procedimento, ds_categoria,
    nr_carencia_dias, vl_pct_copag, fl_cobertura,
    ds_observacao, st_reg_cob, dt_cad_reg_cob
)
SELECT pl.id_pl_conv, proc.ds_procedimento, proc.ds_categoria,
       proc.nr_carencia_dias,
       CASE
           WHEN pl.cd_pl_conv LIKE '%-PRM' OR pl.cd_pl_conv IN ('BRA-ESP','SUL-PRT','PRT-OUR','GLD-MAS') THEN 0.00
           WHEN pl.cd_pl_conv IN ('UNI-PLE','AMI-SAU','BRA-EFE','SUL-ESP','NTD-ADV','HAP-PLE','PRT-PRA','GLD-PLU') THEN proc.vl_pct_pleno
           ELSE proc.vl_pct_basico
       END AS vl_pct_copag,
       CASE
           WHEN pl.tp_cobertura = 'AMBULATORIAL' AND proc.fl_apenas_completo = 'S' THEN 'N'
           ELSE 'S'
       END AS fl_cobertura,
       CASE
           WHEN pl.tp_cobertura = 'AMBULATORIAL' AND proc.fl_apenas_completo = 'S'
               THEN 'Procedimento não coberto neste tipo de plano. Considere upgrade para plano completo.'
           ELSE proc.ds_observacao
       END AS ds_observacao,
       'A', NOW()
FROM hsg.tb_pl_conv pl
CROSS JOIN (VALUES
    ('Consulta médica eletiva',          'Consultas',   0,   30.00, 15.00, 'N', 'Consultas eletivas em rede credenciada. Sem limite mensal.'),
    ('Consulta com especialista',        'Consultas',   0,   30.00, 15.00, 'N', 'Especialidades médicas em rede credenciada.'),
    ('Atendimento de urgência/emergência','Urgência',   1,   0.00,  0.00,  'N', 'Pronto-socorro 24h. Carência mínima legal de 24 horas.'),
    ('Exames laboratoriais',             'Exames',      30,  20.00, 10.00, 'N', 'Hemograma, bioquímica, sorologia e exames clínicos básicos.'),
    ('Exames de imagem simples',         'Exames',      90,  20.00, 10.00, 'N', 'Raio-X, ultrassonografia e mamografia.'),
    ('Exames de imagem avançados',       'Exames',      180, 30.00, 15.00, 'S', 'Tomografia computadorizada, ressonância magnética e PET-CT.'),
    ('Procedimentos ambulatoriais',      'Procedimentos',60, 20.00, 10.00, 'N', 'Pequenos procedimentos em consultório ou ambulatório.'),
    ('Fisioterapia',                     'Terapias',    60,  20.00, 10.00, 'N', 'Até 40 sessões por ano. Necessária autorização prévia.'),
    ('Internação clínica',               'Internação',  180, 0.00,  0.00,  'S', 'Internação hospitalar para tratamento clínico.'),
    ('Internação em UTI',                'Internação',  180, 0.00,  0.00,  'S', 'Unidade de Terapia Intensiva sem limite de diárias.'),
    ('Cirurgia eletiva',                 'Cirurgia',    180, 0.00,  0.00,  'S', 'Cirurgias agendadas em rede credenciada com autorização prévia.'),
    ('Cirurgia de urgência',             'Cirurgia',    1,   0.00,  0.00,  'S', 'Cirurgias de emergência. Carência mínima legal.'),
    ('Parto normal',                     'Obstetrícia', 300, 0.00,  0.00,  'S', 'Parto natural com acompanhamento obstétrico.'),
    ('Parto cesárea',                    'Obstetrícia', 300, 0.00,  0.00,  'S', 'Parto cirúrgico com indicação médica.'),
    ('Tratamento oncológico',            'Oncologia',   180, 0.00,  0.00,  'S', 'Quimioterapia, radioterapia e acompanhamento oncológico.'),
    ('Hemodiálise',                      'Terapias',    180, 0.00,  0.00,  'S', 'Tratamento de insuficiência renal crônica.')
) AS proc(ds_procedimento, ds_categoria, nr_carencia_dias, vl_pct_basico, vl_pct_pleno, fl_apenas_completo, ds_observacao)
ON CONFLICT (id_pl_conv, ds_procedimento) DO NOTHING;
