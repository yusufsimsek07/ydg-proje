package com.haccp.audit.repository;

import com.haccp.audit.entity.ChecklistTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChecklistTemplateRepository extends JpaRepository<ChecklistTemplate, Long> {
    Optional<ChecklistTemplate> findByActiveTrue();
}
