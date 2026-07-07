package com.petvital.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String senha;

    @NotBlank(message = "O perfil é obrigatório")
    @Pattern(regexp = "^(ADMIN|VETERINARIO|RECEPCAO|AUXILIAR|FINANCEIRO)$",
             message = "Perfil inválido. Use: ADMIN, VETERINARIO, RECEPCAO, AUXILIAR ou FINANCEIRO")
    private String perfil;

    /**
     * ID da clínica à qual o usuário pertence.
     * Obrigatório quando master = false.
     * Ignorado (pode ser nulo) quando master = true.
     */
    private Long clinicaId;

    /**
     * Somente usuários master podem criar outros usuários master.
     * Se não informado, o padrão é false.
     */
    private Boolean master = false;
}
