package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    Optional<Consulta> findByIdAndClinicaId(Long id, Long clinicaId);
    List<Consulta> findAllByClinicaIdOrderByDataAddDesc(Long clinicaId);
    List<Consulta> findAllByClinicaIdAndVeterinarioIdOrderByDataAddDesc(Long clinicaId, Long veterinarioId);
}
