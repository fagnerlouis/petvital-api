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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutor")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 11)
    private String cpf;

    private String email;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "aceita_comunicacao_informativa", nullable = false)
    private Boolean aceitaComunicacaoInformativa = false;

    @Column(nullable = false)
    private Boolean ativo = true;

    @CreatedDate
    @Column(name = "data_add", nullable = false, updatable = false)
    private LocalDateTime dataAdd;

    @LastModifiedDate
    @Column(name = "data_alt")
    private LocalDateTime dataAlt;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorTelefone> telefones = new ArrayList<>();

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorEndereco> enderecos = new ArrayList<>();

    public void addTelefone(TutorTelefone telefone) {
        telefones.add(telefone);
        telefone.setTutor(this);
    }

    public void addEndereco(TutorEndereco endereco) {
        enderecos.add(endereco);
        endereco.setTutor(this);
    }
}
