CREATE TABLE clinica (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255),
    tipo_tenant VARCHAR(2) NOT NULL CHECK (tipo_tenant IN ('PF', 'PJ')),
    documento_fiscal VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255),
    whatsapp_api_token VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP
);

COMMENT ON TABLE clinica IS 'Tabela raiz do sistema (Tenant). Representa uma Clínica (PJ) ou Veterinário Autônomo (PF).';
COMMENT ON COLUMN clinica.nome IS 'Razão Social (PJ) ou Nome Completo (PF).';
COMMENT ON COLUMN clinica.nome_fantasia IS 'Nome Comercial (Apenas para PJ).';
COMMENT ON COLUMN clinica.tipo_tenant IS 'PF para Pessoa Física, PJ para Pessoa Jurídica (RN002).';
COMMENT ON COLUMN clinica.documento_fiscal IS 'CPF ou CNPJ. Deve ser único (RN003).';
COMMENT ON COLUMN clinica.whatsapp_api_token IS 'Token da API do WhatsApp. Deve ser gravado criptografado pela aplicação (RN014).';

CREATE TABLE clinica_telefone (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id) ON DELETE CASCADE,
    numero VARCHAR(20) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- Ex: WHATSAPP, CELULAR, FIXO
    is_principal BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE clinica_telefone IS 'Telefones de contato da clínica ou veterinário.';

CREATE TABLE clinica_endereco (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id) ON DELETE CASCADE,
    cep VARCHAR(10) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(50),
    complemento VARCHAR(255),
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL
);

COMMENT ON TABLE clinica_endereco IS 'Endereço da clínica ou local de atendimento do veterinário.';
