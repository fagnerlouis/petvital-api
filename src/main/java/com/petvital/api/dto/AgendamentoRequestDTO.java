package com.petvital.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgendamentoRequestDTO {

    @NotNull(message = "O ID da clínica é obrigatório")
    private Long clinicaId;

    @NotNull(message = "O ID do animal é obrigatório")
    private Long animalId;

    private Long veterinarioId; // Opcional na criação

    @NotNull(message = "A data/hora de início é obrigatória")
    @Future(message = "O agendamento deve ser em uma data futura")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "A data/hora de fim é obrigatória")
    private LocalDateTime dataHoraFim;

    @NotBlank(message = "O tipo de atendimento é obrigatório")
    @Pattern(regexp = "^(CONSULTA|RETORNO|VACINA|CIRURGIA|EXAME|OUTRO)$",
             message = "Tipo inválido. Use: CONSULTA, RETORNO, VACINA, CIRURGIA, EXAME ou OUTRO")
    private String tipoAtendimento;

    private String observacoes;
}
