CREATE TABLE tutor (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    email VARCHAR(255),
    data_nascimento DATE,
    aceita_comunicacao_informativa BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP,
    -- CPF único por clínica (o mesmo tutor pode existir em clínicas diferentes)
    CONSTRAINT uq_tutor_cpf_clinica UNIQUE (clinica_id, cpf)
);

COMMENT ON TABLE tutor IS 'Proprietários dos animais. Vinculados a uma clínica (tenant).';
COMMENT ON COLUMN tutor.cpf IS 'CPF do tutor. Único dentro da mesma clínica (RN003).';
COMMENT ON COLUMN tutor.aceita_comunicacao_informativa IS 'Consentimento LGPD para receber mensagens informativas/promocionais (RN010).';

CREATE TABLE tutor_telefone (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT NOT NULL REFERENCES tutor(id) ON DELETE CASCADE,
    numero VARCHAR(20) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- Ex: WHATSAPP, CELULAR, FIXO
    is_principal BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE tutor_telefone IS 'Telefones de contato do tutor.';

CREATE TABLE tutor_endereco (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT NOT NULL REFERENCES tutor(id) ON DELETE CASCADE,
    cep VARCHAR(10) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(50),
    complemento VARCHAR(255),
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL
);

COMMENT ON TABLE tutor_endereco IS 'Endereços do tutor.';
