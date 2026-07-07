package com.petvital.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TutorResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private LocalDate dataNascimento;
    private Boolean aceitaComunicacaoInformativa;
    private Boolean ativo;
    private Long clinicaId;
    private LocalDateTime dataAdd;
    private List<TelefoneResponseDTO> telefones;
    private List<EnderecoResponseDTO> enderecos;

    @Getter
    @Setter
    public static class TelefoneResponseDTO {
        private Long id;
        private String numero;
        private String tipo;
        private Boolean isPrincipal;
    }

    @Getter
    @Setter
    public static class EnderecoResponseDTO {
        private Long id;
        private String cep;
        private String logradouro;
        private String numero;
        private String complemento;
        private String bairro;
        private String cidade;
        private String estado;
    }
}
