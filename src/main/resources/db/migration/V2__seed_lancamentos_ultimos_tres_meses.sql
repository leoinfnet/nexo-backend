-- Massa de dados didática para consultas, filtros e testes de agentes.
-- As datas são calculadas no momento em que a migration é executada.

INSERT INTO lancamentos (
    descricao,
    valor,
    tipo,
    categoria,
    data_lancamento,
    observacao
)
SELECT
    CASE serie.numero % 4
        WHEN 0 THEN 'Receita de mensalidade de consultoria'
        WHEN 1 THEN 'Receita de projeto de automação'
        WHEN 2 THEN 'Receita de treinamento corporativo'
        ELSE 'Receita de suporte técnico'
    END,
    (1200 + ((serie.numero * 487) % 7800))::NUMERIC(19, 2),
    'RECEITA',
    CASE serie.numero % 4
        WHEN 0 THEN 'Consultoria'
        WHEN 1 THEN 'Projetos'
        WHEN 2 THEN 'Treinamentos'
        ELSE 'Suporte'
    END,
    CURRENT_DATE - (serie.numero % 91),
    'Receita gerada para a base didática do Nexo'
FROM generate_series(0, 89) AS serie(numero);

INSERT INTO lancamentos (
    descricao,
    valor,
    tipo,
    categoria,
    data_lancamento,
    observacao
)
SELECT
    CASE serie.numero % 12
        WHEN 0 THEN 'Aluguel do escritório'
        WHEN 1 THEN 'Conta de energia elétrica'
        WHEN 2 THEN 'Serviços de internet e telefonia'
        WHEN 3 THEN 'Compra de material de escritório'
        WHEN 4 THEN 'Assinatura de software'
        WHEN 5 THEN 'Deslocamento por aplicativo'
        WHEN 6 THEN 'Hospedagem em viagem corporativa'
        WHEN 7 THEN 'Alimentação da equipe'
        WHEN 8 THEN 'Serviço de contabilidade'
        WHEN 9 THEN 'Campanha de marketing digital'
        WHEN 10 THEN 'Manutenção de equipamentos'
        ELSE 'Capacitação profissional'
    END,
    CASE serie.numero % 12
        WHEN 0 THEN (3500 + (serie.numero % 5) * 150)
        WHEN 1 THEN (420 + (serie.numero % 9) * 27)
        WHEN 2 THEN (260 + (serie.numero % 7) * 19)
        WHEN 3 THEN (80 + (serie.numero % 11) * 23)
        WHEN 4 THEN (45 + (serie.numero % 13) * 31)
        WHEN 5 THEN (22 + (serie.numero % 17) * 8)
        WHEN 6 THEN (350 + (serie.numero % 8) * 115)
        WHEN 7 THEN (35 + (serie.numero % 15) * 12)
        WHEN 8 THEN (700 + (serie.numero % 6) * 90)
        WHEN 9 THEN (300 + (serie.numero % 10) * 125)
        WHEN 10 THEN (120 + (serie.numero % 12) * 48)
        ELSE (180 + (serie.numero % 9) * 75)
    END::NUMERIC(19, 2),
    'DESPESA',
    CASE serie.numero % 12
        WHEN 0 THEN 'Infraestrutura'
        WHEN 1 THEN 'Utilidades'
        WHEN 2 THEN 'Comunicação'
        WHEN 3 THEN 'Material de escritório'
        WHEN 4 THEN 'Software'
        WHEN 5 THEN 'Transporte'
        WHEN 6 THEN 'Viagens'
        WHEN 7 THEN 'Alimentação'
        WHEN 8 THEN 'Serviços profissionais'
        WHEN 9 THEN 'Marketing'
        WHEN 10 THEN 'Manutenção'
        ELSE 'Treinamentos'
    END,
    CURRENT_DATE - (serie.numero % 91),
    CASE
        WHEN serie.numero % 17 = 0 THEN 'Despesa recorrente'
        WHEN serie.numero % 13 = 0 THEN 'Pagamento realizado por cartão corporativo'
        ELSE 'Despesa gerada para a base didática do Nexo'
    END
FROM generate_series(1, 360) AS serie(numero);
