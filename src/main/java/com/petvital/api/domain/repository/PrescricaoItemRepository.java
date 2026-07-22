package com.petvital.api.domain.repository;

import com.petvital.api.domain.model.PrescricaoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescricaoItemRepository extends JpaRepository<PrescricaoItem, Long> {
}
