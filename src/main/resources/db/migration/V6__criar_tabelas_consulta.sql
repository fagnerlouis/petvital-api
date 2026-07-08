CREATE TABLE consulta (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    agendamento_id BIGINT REFERENCES agendamento(id), -- Opcional: pode existir consulta sem agendamento prévio (emergência)
    veterinario_id BIGINT NOT NULL REFERENCES usuario(id),
    motivo_consulta TEXT NOT NULL,
    anamnese TEXT,
    exame_fisico TEXT,
    diagnostico TEXT,
    conduta TEXT,
    historico_previo TEXT,
    versao INTEGER NOT NULL DEFAULT 1,
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP
);

COMMENT ON TABLE consulta IS 'Prontuário clínico veterinário (RN006).';
COMMENT ON COLUMN consulta.versao IS 'Versão atual do prontuário. Incrementada a cada alteração.';

CREATE TABLE consulta_historico (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    consulta_id BIGINT NOT NULL REFERENCES consulta(id),
    usuario_alteracao_id BIGINT NOT NULL REFERENCES usuario(id),
    motivo_consulta TEXT NOT NULL,
    anamnese TEXT,
    exame_fisico TEXT,
    diagnostico TEXT,
    conduta TEXT,
    historico_previo TEXT,
    versao INTEGER NOT NULL,
    data_alteracao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE consulta_historico IS 'Histórico de alterações do prontuário (RN006).';
COMMENT ON COLUMN consulta_historico.usuario_alteracao_id IS 'Usuário que fez a modificação que arquivou esta versão.';
COMMENT ON COLUMN consulta_historico.versao IS 'A versão salva correspondente do registro antes da alteração.';
