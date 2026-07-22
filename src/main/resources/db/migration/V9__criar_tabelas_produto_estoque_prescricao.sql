CREATE TABLE produto (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    unidade_medida VARCHAR(20),
    estoque_minimo NUMERIC(10, 2) DEFAULT 0,
    preco_venda NUMERIC(10, 2),
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE estoque_movimento (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    produto_id BIGINT NOT NULL REFERENCES produto(id),
    tipo_movimento VARCHAR(50) NOT NULL,
    quantidade NUMERIC(10, 2) NOT NULL,
    lote VARCHAR(50),
    validade DATE,
    referencia_id BIGINT,
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id)
);

CREATE TABLE prescricao (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    consulta_id BIGINT NOT NULL REFERENCES consulta(id),
    veterinario_id BIGINT NOT NULL REFERENCES usuario(id),
    tipo_receita VARCHAR(50) NOT NULL,
    instrucoes_gerais TEXT,
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP
);

CREATE TABLE prescricao_item (
    id BIGSERIAL PRIMARY KEY,
    prescricao_id BIGINT NOT NULL REFERENCES prescricao(id),
    produto_id BIGINT NOT NULL REFERENCES produto(id),
    dosagem VARCHAR(100) NOT NULL,
    frequencia VARCHAR(100) NOT NULL,
    duracao VARCHAR(100),
    observacoes TEXT
);

COMMENT ON TABLE produto IS 'Cadastro base de medicamentos, materiais e serviços da clínica.';
COMMENT ON TABLE estoque_movimento IS 'Registro manual ou automático de entradas e saídas de estoque.';
COMMENT ON TABLE prescricao IS 'Cabeçalho do receituário médico gerado em uma consulta.';
COMMENT ON TABLE prescricao_item IS 'Itens (medicamentos/produtos) prescritos na receita.';
