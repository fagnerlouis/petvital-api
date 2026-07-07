CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT REFERENCES clinica(id), -- NULLABLE: nulo quando master = true
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL CHECK (perfil IN ('ADMIN', 'VETERINARIO', 'RECEPCAO', 'AUXILIAR', 'FINANCEIRO')),
    master BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP
);

COMMENT ON TABLE usuario IS 'Usuários do sistema. Vinculados a uma clínica (clinica_id), exceto quando master=true (Super Admin).';
COMMENT ON COLUMN usuario.clinica_id IS 'FK para clinica. Nulo apenas quando master=true (RN026).';
COMMENT ON COLUMN usuario.perfil IS 'Perfil de acesso: ADMIN, VETERINARIO, RECEPCAO, AUXILIAR, FINANCEIRO (RN004).';
COMMENT ON COLUMN usuario.master IS 'Quando true, o usuário é Super Administrador e pode acessar todos os tenants (RN026, RN027).';
COMMENT ON COLUMN usuario.senha_hash IS 'Senha armazenada como hash BCrypt. Nunca em texto puro.';
