package com.petvital.api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clinica")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(name = "tipo_tenant", nullable = false, length = 2)
    private String tipoTenant;

    @Column(name = "documento_fiscal", nullable = false, unique = true, length = 14)
    private String documentoFiscal;

    private String email;

    @Column(name = "whatsapp_api_token")
    private String whatsappApiToken;

    @Column(nullable = false)
    private Boolean ativo = true;

    @CreatedDate
    @Column(name = "data_add", nullable = false, updatable = false)
    private LocalDateTime dataAdd;

    @LastModifiedDate
    @Column(name = "data_alt")
    private LocalDateTime dataAlt;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClinicaTelefone> telefones = new ArrayList<>();

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClinicaEndereco> enderecos = new ArrayList<>();
    
    public void addTelefone(ClinicaTelefone telefone) {
        telefones.add(telefone);
        telefone.setClinica(this);
    }
    
    public void addEndereco(ClinicaEndereco endereco) {
        enderecos.add(endereco);
        endereco.setClinica(this);
    }
}
