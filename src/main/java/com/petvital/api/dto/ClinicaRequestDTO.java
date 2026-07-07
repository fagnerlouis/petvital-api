package com.petvital.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ClinicaRequestDTO {
    
    @NotBlank(message = "O nome ou razão social é obrigatório")
    private String nome;
    
    private String nomeFantasia;
    
    @NotBlank(message = "O tipo do tenant é obrigatório")
    @Pattern(regexp = "^(PF|PJ)$", message = "O tipo deve ser PF ou PJ")
    private String tipoTenant;
    
    @NotBlank(message = "O documento fiscal (CPF/CNPJ) é obrigatório")
    private String documentoFiscal;
    
    private String email;
    
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
