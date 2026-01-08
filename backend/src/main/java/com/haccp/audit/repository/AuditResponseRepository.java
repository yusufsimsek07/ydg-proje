package com.haccp.audit.repository;

import com.haccp.audit.entity.AuditResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditResponseRepository extends JpaRepository<AuditResponse, Long> {
    List<AuditResponse> findByAuditId(Long auditId);
    Optional<AuditResponse> findByAuditIdAndItemId(Long auditId, Long itemId);
}
