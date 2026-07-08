package com.petvital.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VacinaRequestDTO {

    @NotNull(message = "O ID da clínica é obrigatório")
    private Long clinicaId;

    @NotNull(message = "O ID do animal é obrigatório")
    private Long animalId;

    @NotNull(message = "O ID do profissional é obrigatório")
    private Long profissionalId;

    @NotBlank(message = "O nome da vacina é obrigatório")
    private String nomeVacina;

    @NotNull(message = "A data de aplicação é obrigatória")
    private LocalDate dataAplicacao;

    private LocalDate dataProximoReforco;
    private String lote;
    private String fabricante;
    private LocalDate validadeVacina;
}
