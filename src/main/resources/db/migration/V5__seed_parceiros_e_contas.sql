INSERT INTO parceiros (nome, documento, email, telefone, tipo) VALUES
    ('Alfa Contabilidade', '10000000000101', 'financeiro@alfacontabilidade.com.br', '(21) 3000-1001', 'FORNECEDOR'),
    ('Nuvem Sistemas', '10000000000284', 'contato@nuvemsistemas.com.br', '(11) 3000-1002', 'FORNECEDOR'),
    ('Conecta Telecom', '10000000000365', 'corporativo@conectatelecom.com.br', '(11) 3000-1003', 'FORNECEDOR'),
    ('Ponto Office Suprimentos', '10000000000446', 'vendas@pontooffice.com.br', '(21) 3000-1004', 'FORNECEDOR'),
    ('Rota Certa Viagens', '10000000000527', 'empresas@rotacerta.com.br', '(31) 3000-1005', 'FORNECEDOR'),
    ('Mídia Norte Marketing', '10000000000608', 'atendimento@midianorte.com.br', '(11) 3000-1006', 'FORNECEDOR'),
    ('Energia Predial Serviços', '10000000000799', 'cobranca@energiapredial.com.br', '(21) 3000-1007', 'FORNECEDOR'),
    ('Sabor & Equipe Alimentação', '10000000000870', 'pedidos@saboreequipe.com.br', '(21) 3000-1008', 'FORNECEDOR'),
    ('TechCare Manutenção', '10000000000950', 'suporte@techcare.com.br', '(11) 3000-1009', 'FORNECEDOR'),
    ('Edifício Central Administração', '10000000001094', 'administracao@edificiocentral.com.br', '(21) 3000-1010', 'FORNECEDOR'),
    ('Horizonte Educação', '20000000000101', 'financeiro@horizonte.edu.br', '(21) 4000-2001', 'CLIENTE'),
    ('Vértice Logística', '20000000000292', 'contas@verticelogistica.com.br', '(11) 4000-2002', 'CLIENTE'),
    ('Clínica Bem Viver', '20000000000373', 'administrativo@bemviver.com.br', '(21) 4000-2003', 'CLIENTE'),
    ('Grupo Aurora Varejo', '20000000000454', 'pagamentos@grupoaurora.com.br', '(31) 4000-2004', 'CLIENTE'),
    ('Instituto Saber Mais', '20000000000535', 'financeiro@sabermais.org.br', '(21) 4000-2005', 'CLIENTE'),
    ('Fábrica Nova Era', '20000000000616', 'controladoria@novaera.com.br', '(11) 4000-2006', 'CLIENTE'),
    ('Rede Atlântico Hotéis', '20000000000705', 'compras@atlantico.com.br', '(21) 4000-2007', 'CLIENTE'),
    ('Construtora Pilar', '20000000000888', 'financeiro@construtorapilar.com.br', '(31) 4000-2008', 'CLIENTE'),
    ('Cooperativa Campo Verde', '20000000000969', 'tesouraria@campoverde.coop.br', '(19) 4000-2009', 'CLIENTE'),
    ('Laboratório Prisma', '20000000001006', 'administracao@laboratorioprisma.com.br', '(11) 4000-2010', 'CLIENTE'),
    ('Integra Soluções Empresariais', '30000000000101', 'financeiro@integrasolucoes.com.br', '(21) 5000-3001', 'AMBOS'),
    ('Plataforma Evoluir', '30000000000284', 'contato@evoluir.com.br', '(11) 5000-3002', 'AMBOS'),
    ('Hub Criativo Rio', '30000000000365', 'administracao@hubcriativo.com.br', '(21) 5000-3003', 'AMBOS'),
    ('Academia Digital Brasil', '30000000000446', 'financeiro@academiadigital.com.br', '(31) 5000-3004', 'AMBOS');

WITH fornecedores AS (
    SELECT array_agg(id ORDER BY id) AS ids
    FROM parceiros
    WHERE tipo IN ('FORNECEDOR', 'AMBOS')
), base AS (
    SELECT
        serie.numero,
        CURRENT_DATE + ((serie.numero % 121) - 60) AS vencimento,
        fornecedores.ids[(serie.numero % cardinality(fornecedores.ids)) + 1] AS parceiro_id
    FROM generate_series(1, 120) AS serie(numero)
    CROSS JOIN fornecedores
)
INSERT INTO contas (
    descricao, valor, tipo, categoria, vencimento, status, data_pagamento,
    recorrencia, observacao, parceiro_id
)
SELECT
    CASE base.numero % 10
        WHEN 0 THEN 'Aluguel e condomínio'
        WHEN 1 THEN 'Licenças de software'
        WHEN 2 THEN 'Internet corporativa'
        WHEN 3 THEN 'Serviços contábeis'
        WHEN 4 THEN 'Material de escritório'
        WHEN 5 THEN 'Campanha de marketing'
        WHEN 6 THEN 'Manutenção de equipamentos'
        WHEN 7 THEN 'Passagens e hospedagem'
        WHEN 8 THEN 'Energia elétrica'
        ELSE 'Alimentação da equipe'
    END,
    (80 + ((base.numero * 137) % 5200))::NUMERIC(19, 2),
    'PAGAR',
    CASE base.numero % 10
        WHEN 0 THEN 'Infraestrutura'
        WHEN 1 THEN 'Software'
        WHEN 2 THEN 'Comunicação'
        WHEN 3 THEN 'Serviços profissionais'
        WHEN 4 THEN 'Material de escritório'
        WHEN 5 THEN 'Marketing'
        WHEN 6 THEN 'Manutenção'
        WHEN 7 THEN 'Viagens'
        WHEN 8 THEN 'Utilidades'
        ELSE 'Alimentação'
    END,
    base.vencimento,
    CASE WHEN base.numero % 10 = 0 THEN 'CANCELADA' WHEN base.numero % 4 = 0 THEN 'PAGA' ELSE 'PENDENTE' END,
    CASE WHEN base.numero % 4 = 0 AND base.numero % 10 <> 0
        THEN LEAST(CURRENT_DATE, base.vencimento + ((base.numero % 5) - 2))
        ELSE NULL
    END,
    CASE WHEN base.numero % 5 = 0 THEN 'MENSAL' ELSE 'UNICA' END,
    'Conta a pagar da base didática do Nexo',
    base.parceiro_id
FROM base;

WITH clientes AS (
    SELECT array_agg(id ORDER BY id) AS ids
    FROM parceiros
    WHERE tipo IN ('CLIENTE', 'AMBOS')
), base AS (
    SELECT
        serie.numero,
        CURRENT_DATE + ((serie.numero % 121) - 60) AS vencimento,
        clientes.ids[(serie.numero % cardinality(clientes.ids)) + 1] AS parceiro_id
    FROM generate_series(1, 90) AS serie(numero)
    CROSS JOIN clientes
)
INSERT INTO contas (
    descricao, valor, tipo, categoria, vencimento, status, data_pagamento,
    recorrencia, observacao, parceiro_id
)
SELECT
    CASE base.numero % 4
        WHEN 0 THEN 'Mensalidade de consultoria'
        WHEN 1 THEN 'Projeto de automação'
        WHEN 2 THEN 'Treinamento corporativo'
        ELSE 'Contrato de suporte técnico'
    END,
    (900 + ((base.numero * 421) % 9100))::NUMERIC(19, 2),
    'RECEBER',
    CASE base.numero % 4
        WHEN 0 THEN 'Consultoria'
        WHEN 1 THEN 'Projetos'
        WHEN 2 THEN 'Treinamentos'
        ELSE 'Suporte'
    END,
    base.vencimento,
    CASE WHEN base.numero % 13 = 0 THEN 'CANCELADA' WHEN base.numero % 3 = 0 THEN 'PAGA' ELSE 'PENDENTE' END,
    CASE WHEN base.numero % 3 = 0 AND base.numero % 13 <> 0
        THEN LEAST(CURRENT_DATE, base.vencimento + ((base.numero % 7) - 3))
        ELSE NULL
    END,
    CASE WHEN base.numero % 4 = 0 THEN 'MENSAL' ELSE 'UNICA' END,
    'Conta a receber da base didática do Nexo',
    base.parceiro_id
FROM base;
