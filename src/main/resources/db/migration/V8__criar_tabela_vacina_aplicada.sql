CREATE TABLE vacina_aplicada (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    animal_id BIGINT NOT NULL REFERENCES animal(id),
    profissional_id BIGINT NOT NULL REFERENCES usuario(id),
    nome_vacina VARCHAR(100) NOT NULL,
    data_aplicacao DATE NOT NULL,
    data_proximo_reforco DATE,
    lote VARCHAR(50),
    fabricante VARCHAR(100),
    validade_vacina DATE,
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP
);

COMMENT ON TABLE vacina_aplicada IS 'Registro de vacinas aplicadas ao animal (RN009, RN012). Independente do controle de estoque.';
COMMENT ON COLUMN vacina_aplicada.profissional_id IS 'Usuário/Profissional que aplicou a vacina.';
COMMENT ON COLUMN vacina_aplicada.data_proximo_reforco IS 'Utilizado futuramente para disparar lembretes (RN009).';
