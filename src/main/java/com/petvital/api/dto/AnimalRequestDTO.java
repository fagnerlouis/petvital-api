package com.petvital.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class AnimalRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A espécie é obrigatória")
    private String especie;

    private String raca;

    @NotBlank(message = "O sexo é obrigatório")
    @Pattern(regexp = "^(MACHO|FEMEA)$", message = "Sexo deve ser MACHO ou FEMEA")
    private String sexo;

    private String cor;
    private String pelagem;
    private LocalDate dataNascimento;
    private String microchip;
    private String alergias;
    private String doencasCronicas;

    @NotNull(message = "O ID da clínica é obrigatório")
    private Long clinicaId;

    @NotNull(message = "O ID do tutor principal é obrigatório")
    private Long tutorPrincipalId;

    /**
     * IDs dos tutores secundários (opcional).
     * Implementa o relacionamento N:N da RN005.
     */
    private Set<Long> tutoresSecundariosIds;
}
