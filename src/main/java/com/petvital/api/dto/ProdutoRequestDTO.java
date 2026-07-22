package com.petvital.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoRequestDTO {
    @NotNull(message = "O ID da clínica é obrigatório")
    private Long clinicaId;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O tipo é obrigatório (ex: MEDICAMENTO, MATERIAL)")
    private String tipo;

    private String unidadeMedida;
    private BigDecimal estoqueMinimo;
    private BigDecimal precoVenda;
}
