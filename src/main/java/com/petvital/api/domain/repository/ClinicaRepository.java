package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.Clinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, Long> {
    boolean existsByDocumentoFiscal(String documentoFiscal);
}
