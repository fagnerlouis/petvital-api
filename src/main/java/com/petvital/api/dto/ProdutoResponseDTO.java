package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private String tipo;
    private String unidadeMedida;
    private BigDecimal estoqueMinimo;
    private BigDecimal precoVenda;
    private Boolean ativo;
}
