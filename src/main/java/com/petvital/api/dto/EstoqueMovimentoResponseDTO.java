package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EstoqueMovimentoResponseDTO {
    private Long id;
    private Long produtoId;
    private String tipoMovimento;
    private BigDecimal quantidade;
    private String lote;
    private LocalDate validade;
    private Long referenciaId;
    private LocalDateTime dataAdd;
    private UsuarioResumoDTO usuario;

    @Getter
    @Setter
    public static class UsuarioResumoDTO {
        private Long id;
        private String nome;
    }
}
