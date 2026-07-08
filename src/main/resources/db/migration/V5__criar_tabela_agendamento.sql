CREATE TABLE agendamento (
    id BIGSERIAL PRIMARY KEY,
    clinica_id BIGINT NOT NULL REFERENCES clinica(id),
    animal_id BIGINT NOT NULL REFERENCES animal(id),
    veterinario_id BIGINT REFERENCES usuario(id), -- Nullable: agendamento pode ser criado antes de definir o veterinário
    data_hora_inicio TIMESTAMP NOT NULL,
    data_hora_fim TIMESTAMP NOT NULL,
    tipo_atendimento VARCHAR(50) NOT NULL CHECK (tipo_atendimento IN ('CONSULTA', 'RETORNO', 'VACINA', 'CIRURGIA', 'EXAME', 'OUTRO')),
    status VARCHAR(50) NOT NULL DEFAULT 'AGENDADO' CHECK (status IN ('AGENDADO', 'CONFIRMADO', 'ATENDIDO', 'FALTOU', 'CANCELADO')),
    observacoes TEXT,
    data_add TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_alt TIMESTAMP,
    -- Garante que o horário de fim é sempre após o início
    CONSTRAINT chk_agendamento_horario CHECK (data_hora_fim > data_hora_inicio)
);

COMMENT ON TABLE agendamento IS 'Agendamentos de atendimentos veterinários. Controla o fluxo da agenda da clínica.';
COMMENT ON COLUMN agendamento.veterinario_id IS 'Veterinário responsável. Pode ser definido após a criação do agendamento.';
COMMENT ON COLUMN agendamento.tipo_atendimento IS 'CONSULTA, RETORNO, VACINA, CIRURGIA, EXAME ou OUTRO.';
COMMENT ON COLUMN agendamento.status IS 'Ciclo de vida: AGENDADO → CONFIRMADO → ATENDIDO. Ou FALTOU/CANCELADO.';
