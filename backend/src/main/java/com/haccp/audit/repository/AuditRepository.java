package com.haccp.audit.repository;

import com.haccp.audit.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    @EntityGraph(attributePaths = {"facility", "createdBy", "responses", "responses.item", "nonConformities", "nonConformities.item", "nonConformities.correctiveActions"})
    Optional<Audit> findById(Long id);

    List<Audit> findByCreatedByUsernameOrderByAuditDateDesc(String username);
}
