package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConsultaHistoricoResponseDTO {
    private Long id;
    private Long consultaId;
    
    private UsuarioAlteracaoDTO usuarioAlteracao;
    
    private String motivoConsulta;
    private String anamnese;
    private String exameFisico;
    private String diagnostico;
    private String conduta;
    private String historicoPrevio;
    
    private Integer versao;
    private LocalDateTime dataAlteracao;

    @Getter
    @Setter
    public static class UsuarioAlteracaoDTO {
        private Long id;
        private String nome;
    }
}
