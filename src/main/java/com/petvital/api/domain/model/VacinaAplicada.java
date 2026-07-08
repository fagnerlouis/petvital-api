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

@Entity
@Table(name = "vacina_aplicada")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VacinaAplicada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", nullable = false)
    private Usuario profissional;

    @Column(name = "nome_vacina", nullable = false, length = 100)
    private String nomeVacina;

    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Column(name = "data_proximo_reforco")
    private LocalDate dataProximoReforco;

    @Column(length = 50)
    private String lote;

    @Column(length = 100)
    private String fabricante;

    @Column(name = "validade_vacina")
    private LocalDate validadeVacina;

    @CreatedDate
    @Column(name = "data_add", nullable = false, updatable = false)
    private LocalDateTime dataAdd;

    @LastModifiedDate
    @Column(name = "data_alt")
    private LocalDateTime dataAlt;
}
