package com.petvital.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private String nome;
    private String perfil;
    private Boolean master;

    public AuthResponseDTO(String token, String nome, String perfil, Boolean master) {
        this.token = token;
        this.nome = nome;
        this.perfil = perfil;
        this.master = master;
    }
}
