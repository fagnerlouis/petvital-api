package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescricaoRepository extends JpaRepository<Prescricao, Long> {
    List<Prescricao> findAllByConsultaIdAndClinicaId(Long consultaId, Long clinicaId);
}
