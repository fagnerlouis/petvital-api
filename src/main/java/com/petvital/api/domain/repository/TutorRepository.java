package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
    boolean existsByClinicaIdAndCpf(Long clinicaId, String cpf);
    Optional<Tutor> findByIdAndClinicaId(Long id, Long clinicaId);
    List<Tutor> findAllByClinicaId(Long clinicaId);
}
