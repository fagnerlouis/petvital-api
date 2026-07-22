package com.petvital.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PrescricaoRequestDTO {
    @NotNull(message = "A clínica é obrigatória")
    private Long clinicaId;

    @NotNull(message = "A consulta é obrigatória")
    private Long consultaId;

    @NotBlank(message = "O tipo de receita é obrigatório")
    private String tipoReceita;

    private String instrucoesGerais;

    private List<ItemDTO> itens;

    @Getter
    @Setter
    public static class ItemDTO {
        @NotNull(message = "O produto é obrigatório no item")
        private Long produtoId;
        
        @NotBlank(message = "A dosagem é obrigatória")
        private String dosagem;
        
        @NotBlank(message = "A frequência é obrigatória")
        private String frequencia;
        
        private String duracao;
        private String observacoes;
    }
}
