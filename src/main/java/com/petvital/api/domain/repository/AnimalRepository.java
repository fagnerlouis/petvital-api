package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    boolean existsByClinicaIdAndMicrochip(Long clinicaId, String microchip);
    Optional<Animal> findByIdAndClinicaId(Long id, Long clinicaId);
    List<Animal> findAllByClinicaId(Long clinicaId);
    List<Animal> findAllByClinicaIdAndTutorPrincipalId(Long clinicaId, Long tutorId);
}
