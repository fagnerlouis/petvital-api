package com.petvital.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PesoHistoricoRequestDTO {

    @NotNull(message = "O ID da clínica é obrigatório")
    private Long clinicaId;

    @NotNull(message = "O ID do animal é obrigatório")
    private Long animalId;

    @NotNull(message = "O peso em kg é obrigatório")
    @DecimalMin(value = "0.01", message = "O peso deve ser maior que zero")
    private BigDecimal pesoKg;

    @Min(value = 1, message = "O ECC mínimo é 1")
    @Max(value = 9, message = "O ECC máximo é 9")
    private Integer ecc;
}
