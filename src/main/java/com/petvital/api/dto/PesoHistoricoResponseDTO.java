package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PesoHistoricoResponseDTO {
    private Long id;
    private Long animalId;
    private BigDecimal pesoKg;
    private Integer ecc;
    private UsuarioResumoDTO usuarioRegistro;
    private LocalDateTime dataAdd;

    @Getter
    @Setter
    public static class UsuarioResumoDTO {
        private Long id;
        private String nome;
    }
}
