INSERT INTO hsg.tb_conv (nm_conv, ds_conv, nr_reg_ans, ds_site, st_conv, dt_cad_conv)
VALUES
    ('Unimed',          'Cooperativa de trabalho médico.',                  '000701', 'https://www.unimed.coop.br',     'A', NOW()),
    ('Amil',            'Operadora de planos de saúde.',                    '326305', 'https://www.amil.com.br',        'A', NOW()),
    ('Bradesco Saúde',  'Operadora de planos de saúde do grupo Bradesco.',  '005711', 'https://www.bradescosaude.com.br','A', NOW()),
    ('SulAmérica Saúde','Operadora de planos de saúde da SulAmérica.',      '006246', 'https://www.sulamerica.com.br',  'A', NOW()),
    ('NotreDame Intermédica','Operadora de planos de saúde.',               '359017', 'https://www.gndi.com.br',        'A', NOW()),
    ('Hapvida',         'Operadora de planos de saúde.',                    '368253', 'https://www.hapvida.com.br',     'A', NOW()),
    ('Porto Seguro Saúde','Operadora de planos de saúde Porto Seguro.',     '417173', 'https://www.portoseguro.com.br', 'A', NOW()),
    ('Golden Cross',    'Operadora tradicional de planos de saúde.',        '005754', 'https://www.goldencross.com.br', 'A', NOW())
ON CONFLICT (nm_conv) DO NOTHING;
