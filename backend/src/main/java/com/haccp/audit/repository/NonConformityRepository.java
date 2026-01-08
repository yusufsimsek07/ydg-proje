package com.haccp.audit.repository;

import com.haccp.audit.entity.NonConformity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NonConformityRepository extends JpaRepository<NonConformity, Long> {
    @EntityGraph(attributePaths = {"audit", "item", "correctiveActions"})
    Optional<NonConformity> findById(Long id);

    @EntityGraph(attributePaths = {"audit", "item", "correctiveActions"})
    List<NonConformity> findAll();

    List<NonConformity> findByAuditId(Long auditId);
}
