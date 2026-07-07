package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String perfil;
    private Boolean master;
    private Boolean ativo;
    private Long clinicaId;
    private String clinicaNome;
    private LocalDateTime dataAdd;
}
