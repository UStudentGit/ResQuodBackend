package com.ustudent.resquod.repository;

import com.ustudent.resquod.model.Corporation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorporationRepository extends JpaRepository<Corporation,Long> {
    Optional<Corporation> findById(Long id);
}