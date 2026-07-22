package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByClinicaIdAndAtivoTrueOrderByNomeAsc(Long clinicaId);
}
