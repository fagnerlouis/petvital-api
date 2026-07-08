package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    Optional<Agendamento> findByIdAndClinicaId(Long id, Long clinicaId);

    List<Agendamento> findAllByClinicaIdOrderByDataHoraInicioAsc(Long clinicaId);

    List<Agendamento> findAllByClinicaIdAndAnimalIdOrderByDataHoraInicioAsc(Long clinicaId, Long animalId);

    // Busca agendamentos de um veterinário em um período (para verificar conflitos de horário)
    @Query("""
        SELECT a FROM Agendamento a
        WHERE a.veterinario.id = :veterinarioId
        AND a.status NOT IN ('CANCELADO', 'FALTOU')
        AND a.dataHoraInicio < :fim
        AND a.dataHoraFim > :inicio
    """)
    List<Agendamento> findConflitosDeHorario(
            @Param("veterinarioId") Long veterinarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
