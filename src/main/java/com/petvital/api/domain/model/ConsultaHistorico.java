package com.petvital.api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consulta_historico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_alteracao_id", nullable = false)
    private Usuario usuarioAlteracao;

    @Column(name = "motivo_consulta", nullable = false, columnDefinition = "TEXT")
    private String motivoConsulta;

    @Column(columnDefinition = "TEXT")
    private String anamnese;

    @Column(name = "exame_fisico", columnDefinition = "TEXT")
    private String exameFisico;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String conduta;

    @Column(name = "historico_previo", columnDefinition = "TEXT")
    private String historicoPrevio;

    @Column(nullable = false)
    private Integer versao;

    @Column(name = "data_alteracao", nullable = false, updatable = false)
    private LocalDateTime dataAlteracao = LocalDateTime.now();
}
