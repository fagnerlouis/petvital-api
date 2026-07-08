CREATE TABLE animal (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    tutor_principal_id BIGINT NOT NULL REFERENCES tutor(id),
    nome VARCHAR(255) NOT NULL,
    especie VARCHAR(50) NOT NULL,
    raca VARCHAR(100),
    sexo VARCHAR(10) NOT NULL CHECK (sexo IN ('MACHO', 'FEMEA')),
    cor VARCHAR(50),
    pelagem VARCHAR(50),
    data_nascimento DATE,
    microchip VARCHAR(50),
    alergias TEXT,
    doencas_cronicas TEXT,
    foto_url VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP,
    -- Microchip único por clínica (o mesmo animal pode estar em clínicas diferentes)
    CONSTRAINT uq_animal_microchip_clinica UNIQUE (clinica_id, microchip)
);

COMMENT ON TABLE animal IS 'Pacientes do sistema. Vinculados a uma clínica (tenant) e a um tutor principal.';
COMMENT ON COLUMN animal.sexo IS 'Sexo do animal: MACHO ou FEMEA.';
COMMENT ON COLUMN animal.microchip IS 'Número do microchip. Único dentro da mesma clínica.';
COMMENT ON COLUMN animal.foto_url IS 'URL da foto do animal. Upload implementado futuramente via Supabase/S3.';

-- Tabela de relacionamento N:N entre Animal e Tutores secundários (RN005)
CREATE TABLE tutor_animal (
    tutor_id BIGINT NOT NULL REFERENCES tutor(id) ON DELETE CASCADE,
    animal_id BIGINT NOT NULL REFERENCES animal(id) ON DELETE CASCADE,
    relacao VARCHAR(50),
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tutor_id, animal_id)
);

COMMENT ON TABLE tutor_animal IS 'Relacionamento N:N entre Tutores e Animais. Permite vincular tutores secundários a um animal (RN005).';
COMMENT ON COLUMN tutor_animal.relacao IS 'Ex: Tutor Secundário, Responsável, Passeador.';
