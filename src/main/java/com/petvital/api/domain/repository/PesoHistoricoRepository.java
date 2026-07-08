package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.PesoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PesoHistoricoRepository extends JpaRepository<PesoHistorico, Long> {
    List<PesoHistorico> findAllByAnimalIdAndClinicaIdOrderByDataAddDesc(Long animalId, Long clinicaId);
}
