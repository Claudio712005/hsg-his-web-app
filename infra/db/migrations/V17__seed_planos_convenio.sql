INSERT INTO hsg.tb_pl_conv (
    id_conv, nm_pl_conv, cd_pl_conv, ds_pl_conv, tp_cobertura,
    vl_mensalidade, fl_acomod_individual, st_pl_conv, dt_cad_pl_conv
)
SELECT c.id_conv, v.nm_pl, v.cd_pl, v.ds_pl, v.tp_cob,
       v.vl_mens, v.fl_acom, 'A', NOW()
FROM hsg.tb_conv c
JOIN (VALUES
    -- Unimed
    ('Unimed',           'Unimed Básico',         'UNI-BAS', 'Cobertura ambulatorial nacional com rede credenciada.',                  'AMBULATORIAL', 289.90,  'N'),
    ('Unimed',           'Unimed Pleno',          'UNI-PLE', 'Cobertura completa nacional com internação e exames.',                   'COMPLETO',     489.90,  'N'),
    ('Unimed',           'Unimed Premium',        'UNI-PRM', 'Cobertura completa com acomodação individual e exames especiais.',       'COMPLETO',     789.90,  'S'),
    -- Amil
    ('Amil',             'Amil Fácil',            'AMI-FAC', 'Plano de entrada com cobertura ambulatorial.',                           'AMBULATORIAL', 259.00,  'N'),
    ('Amil',             'Amil Saúde',            'AMI-SAU', 'Cobertura completa com hospitalar e obstetrícia.',                       'COMPLETO',     459.00,  'N'),
    ('Amil',             'Amil Premium',          'AMI-PRM', 'Plano premium com acomodação individual e rede ampliada.',               'COMPLETO',     799.00,  'S'),
    -- Bradesco
    ('Bradesco Saúde',   'Bradesco Top Nacional', 'BRA-TOP', 'Plano nacional com cobertura completa em rede própria.',                 'COMPLETO',     529.00,  'N'),
    ('Bradesco Saúde',   'Bradesco Efetivo',      'BRA-EFE', 'Plano hospitalar e ambulatorial com rede credenciada nacional.',         'COMPLETO',     699.00,  'N'),
    ('Bradesco Saúde',   'Bradesco Especial',     'BRA-ESP', 'Cobertura especial com acomodação individual e tratamentos exclusivos.','COMPLETO',     1099.00, 'S'),
    -- SulAmérica
    ('SulAmérica Saúde', 'SulAmérica Clássico',   'SUL-CLA', 'Cobertura ambulatorial com rede credenciada.',                           'AMBULATORIAL', 299.00,  'N'),
    ('SulAmérica Saúde', 'SulAmérica Especial',   'SUL-ESP', 'Cobertura completa com internação e maternidade.',                       'COMPLETO',     559.00,  'N'),
    ('SulAmérica Saúde', 'SulAmérica Prestige',   'SUL-PRT', 'Plano premium com acomodação individual e exames especializados.',       'COMPLETO',     899.00,  'S'),
    -- NotreDame
    ('NotreDame Intermédica','NotreDame Smart',   'NTD-SMA', 'Plano regional com cobertura essencial.',                                'AMBULATORIAL', 219.00,  'N'),
    ('NotreDame Intermédica','NotreDame Advance', 'NTD-ADV', 'Cobertura completa com hospitalar e obstetrícia.',                       'COMPLETO',     399.00,  'N'),
    ('NotreDame Intermédica','NotreDame Premium', 'NTD-PRM', 'Plano premium com acomodação individual.',                               'COMPLETO',     659.00,  'S'),
    -- Hapvida
    ('Hapvida',          'Hapvida Mix',           'HAP-MIX', 'Plano regional ambulatorial com baixo custo.',                           'AMBULATORIAL', 199.00,  'N'),
    ('Hapvida',          'Hapvida Pleno',         'HAP-PLE', 'Cobertura completa em rede própria.',                                    'COMPLETO',     349.00,  'N'),
    ('Hapvida',          'Hapvida Premium',       'HAP-PRM', 'Plano premium com acomodação individual.',                               'COMPLETO',     579.00,  'S'),
    -- Porto Seguro
    ('Porto Seguro Saúde','Porto Bronze',         'PRT-BRO', 'Plano básico ambulatorial.',                                             'AMBULATORIAL', 279.00,  'N'),
    ('Porto Seguro Saúde','Porto Prata',          'PRT-PRA', 'Cobertura completa com hospitalar.',                                     'COMPLETO',     499.00,  'N'),
    ('Porto Seguro Saúde','Porto Ouro',           'PRT-OUR', 'Plano premium com acomodação individual e benefícios exclusivos.',       'COMPLETO',     849.00,  'S'),
    -- Golden Cross
    ('Golden Cross',     'Golden Essencial',      'GLD-ESS', 'Cobertura ambulatorial tradicional.',                                    'AMBULATORIAL', 319.00,  'N'),
    ('Golden Cross',     'Golden Plus',           'GLD-PLU', 'Cobertura completa com internação.',                                     'COMPLETO',     579.00,  'N'),
    ('Golden Cross',     'Golden Master',         'GLD-MAS', 'Plano premium com acomodação individual e atendimento prioritário.',     'COMPLETO',     959.00,  'S')
) AS v(nm_conv, nm_pl, cd_pl, ds_pl, tp_cob, vl_mens, fl_acom)
  ON c.nm_conv = v.nm_conv
ON CONFLICT (id_conv, nm_pl_conv) DO NOTHING;
