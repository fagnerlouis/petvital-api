package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.ConsultaHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaHistoricoRepository extends JpaRepository<ConsultaHistorico, Long> {
    List<ConsultaHistorico> findAllByConsultaIdAndClinicaIdOrderByVersaoDesc(Long consultaId, Long clinicaId);
}
