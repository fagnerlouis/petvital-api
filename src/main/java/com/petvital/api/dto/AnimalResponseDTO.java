package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class AnimalResponseDTO {
    private Long id;
    private String nome;
    private String especie;
    private String raca;
    private String sexo;
    private String cor;
    private String pelagem;
    private LocalDate dataNascimento;
    private String microchip;
    private String alergias;
    private String doencasCronicas;
    private String fotoUrl;
    private Boolean ativo;
    private Long clinicaId;
    private TutorResumoDTO tutorPrincipal;
    private Set<TutorResumoDTO> tutoresSecundarios;
    private LocalDateTime dataAdd;

    @Getter
    @Setter
    public static class TutorResumoDTO {
        private Long id;
        private String nome;
        private String cpf;
    }
}
