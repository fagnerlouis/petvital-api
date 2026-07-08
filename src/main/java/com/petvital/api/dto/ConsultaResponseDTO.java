package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConsultaResponseDTO {
    private Long id;
    private Long clinicaId;
    private Long agendamentoId;
    
    private VeterinarioResumoDTO veterinario;
    
    private String motivoConsulta;
    private String anamnese;
    private String exameFisico;
    private String diagnostico;
    private String conduta;
    private String historicoPrevio;
    
    private Integer versao;
    private LocalDateTime dataAdd;
    private LocalDateTime dataAlt;

    @Getter
    @Setter
    public static class VeterinarioResumoDTO {
        private Long id;
        private String nome;
        private String crmv;
    }
}
