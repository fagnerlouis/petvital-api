package com.petvital.api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "animal")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_principal_id", nullable = false)
    private Tutor tutorPrincipal;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(length = 100)
    private String raca;

    @Column(nullable = false, length = 10)
    private String sexo;

    @Column(length = 50)
    private String cor;

    @Column(length = 50)
    private String pelagem;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(length = 50)
    private String microchip;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "doencas_cronicas", columnDefinition = "TEXT")
    private String doencasCronicas;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(nullable = false)
    private Boolean ativo = true;

    @CreatedDate
    @Column(name = "data_add", nullable = false, updatable = false)
    private LocalDateTime dataAdd;

    @LastModifiedDate
    @Column(name = "data_alt")
    private LocalDateTime dataAlt;

    // Tutores secundários (N:N) — RN005
    @ManyToMany
    @JoinTable(
        name = "tutor_animal",
        joinColumns = @JoinColumn(name = "animal_id"),
        inverseJoinColumns = @JoinColumn(name = "tutor_id")
    )
    private Set<Tutor> tutoresSecundarios = new HashSet<>();
}
