package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class VacinaResponseDTO {
    private Long id;
    private Long animalId;
    private String nomeVacina;
    private LocalDate dataAplicacao;
    private LocalDate dataProximoReforco;
    private String lote;
    private String fabricante;
    private LocalDate validadeVacina;
    private LocalDateTime dataAdd;

    private ProfissionalResumoDTO profissional;

    @Getter
    @Setter
    public static class ProfissionalResumoDTO {
        private Long id;
        private String nome;
    }
}
