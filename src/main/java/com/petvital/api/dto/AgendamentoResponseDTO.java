package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgendamentoResponseDTO {
    private Long id;
    private Long clinicaId;
    private AnimalResumoDTO animal;
    private VeterinarioResumoDTO veterinario;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String tipoAtendimento;
    private String status;
    private String observacoes;
    private LocalDateTime dataAdd;

    @Getter
    @Setter
    public static class AnimalResumoDTO {
        private Long id;
        private String nome;
        private String especie;
        private String tutorNome;
    }

    @Getter
    @Setter
    public static class VeterinarioResumoDTO {
        private Long id;
        private String nome;
        private String email;
    }
}
