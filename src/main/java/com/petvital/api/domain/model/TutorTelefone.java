package com.petvital.api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tutor_telefone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TutorTelefone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "is_principal", nullable = false)
    private Boolean isPrincipal = false;
}
