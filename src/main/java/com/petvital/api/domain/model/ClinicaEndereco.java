package com.petvital.api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "clinica_endereco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicaEndereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @Column(nullable = false, length = 10)
    private String cep;

    @Column(nullable = false)
    private String logradouro;

    @Column(length = 50)
    private String numero;

    private String complemento;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;
}
