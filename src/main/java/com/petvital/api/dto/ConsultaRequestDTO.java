package com.petvital.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultaRequestDTO {

    @NotNull(message = "O ID da clínica é obrigatório")
    private Long clinicaId;

    private Long agendamentoId; // Opcional (emergências)

    @NotNull(message = "O ID do veterinário é obrigatório")
    private Long veterinarioId;

    @NotBlank(message = "O motivo da consulta é obrigatório")
    private String motivoConsulta;

    private String anamnese;
    private String exameFisico;
    private String diagnostico;
    private String conduta;
    private String historicoPrevio;
}
