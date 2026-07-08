CREATE TABLE peso_historico (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    animal_id BIGINT NOT NULL REFERENCES animal(id),
    peso_kg NUMERIC(5, 2) NOT NULL,
    ecc INTEGER CHECK (ecc >= 1 AND ecc <= 9),
    usuario_registro_id BIGINT NOT NULL REFERENCES usuario(id),
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP
);

COMMENT ON TABLE peso_historico IS 'Histórico de peso e Escala de Condição Corporal do animal (RN007).';
COMMENT ON COLUMN peso_historico.peso_kg IS 'Peso do animal em quilogramas.';
COMMENT ON COLUMN peso_historico.ecc IS 'Escala de Condição Corporal (1 a 9).';
COMMENT ON COLUMN peso_historico.usuario_registro_id IS 'Usuário que aferiu e registrou o peso.';
