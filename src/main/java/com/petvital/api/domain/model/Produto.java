package com.petvital.api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "unidade_medida", length = 20)
    private String unidadeMedida;

    @Column(name = "estoque_minimo", precision = 10, scale = 2)
    private BigDecimal estoqueMinimo;

    @Column(name = "preco_venda", precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(nullable = false)
    private Boolean ativo = true;
}
