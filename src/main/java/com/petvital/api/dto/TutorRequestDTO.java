package com.petvital.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TutorRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos (sem pontos ou traços)")
    private String cpf;

    private String email;

    private LocalDate dataNascimento;

    private Boolean aceitaComunicacaoInformativa = false;

    @NotNull(message = "O ID da clínica é obrigatório")
    private Long clinicaId;

    private List<TelefoneDTO> telefones;

    private List<EnderecoDTO> enderecos;

    @Getter
    @Setter
    public static class TelefoneDTO {
        @NotBlank private String numero;
        @NotBlank private String tipo;
        private Boolean isPrincipal = false;
    }

    @Getter
    @Setter
    public static class EnderecoDTO {
        @NotBlank private String cep;
        @NotBlank private String logradouro;
        private String numero;
        private String complemento;
        @NotBlank private String bairro;
        @NotBlank private String cidade;
        @NotBlank private String estado;
    }
}
