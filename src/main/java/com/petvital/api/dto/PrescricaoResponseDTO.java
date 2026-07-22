package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PrescricaoResponseDTO {
    private Long id;
    private Long consultaId;
    private String tipoReceita;
    private String instrucoesGerais;
    private LocalDateTime dataAdd;
    
    private VeterinarioResumoDTO veterinario;
    private List<ItemResponseDTO> itens;

    @Getter
    @Setter
    public static class VeterinarioResumoDTO {
        private Long id;
        private String nome;
    }

    @Getter
    @Setter
    public static class ItemResponseDTO {
        private Long id;
        private Long produtoId;
        private String nomeProduto; // Trazido do produto para facilitar front-end
        private String dosagem;
        private String frequencia;
        private String duracao;
        private String observacoes;
    }
}
