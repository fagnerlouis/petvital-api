package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.EstoqueMovimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueMovimentoRepository extends JpaRepository<EstoqueMovimento, Long> {
    List<EstoqueMovimento> findAllByProdutoIdAndClinicaIdOrderByDataAddDesc(Long produtoId, Long clinicaId);
}
