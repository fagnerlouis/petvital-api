package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.VacinaAplicada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacinaAplicadaRepository extends JpaRepository<VacinaAplicada, Long> {
    List<VacinaAplicada> findAllByAnimalIdAndClinicaIdOrderByDataAplicacaoDesc(Long animalId, Long clinicaId);
}
