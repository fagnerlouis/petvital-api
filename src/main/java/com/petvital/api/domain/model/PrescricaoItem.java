package com.petvital.api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "prescricao_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescricaoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescricao_id", nullable = false)
    private Prescricao prescricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false, length = 100)
    private String dosagem;

    @Column(nullable = false, length = 100)
    private String frequencia;

    @Column(length = 100)
    private String duracao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
