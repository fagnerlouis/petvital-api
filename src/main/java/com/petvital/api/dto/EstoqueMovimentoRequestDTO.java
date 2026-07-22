package com.petvital.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EstoqueMovimentoRequestDTO {
    @NotNull(message = "A clínica é obrigatória")
    private Long clinicaId;

    @NotNull(message = "O produto é obrigatório")
    private Long produtoId;

    @NotBlank(message = "O tipo de movimento é obrigatório (ENTRADA, SAIDA, AJUSTE)")
    private String tipoMovimento;

    @NotNull(message = "A quantidade é obrigatória")
    private BigDecimal quantidade;

    private String lote;
    private LocalDate validade;
    private Long referenciaId;
}
