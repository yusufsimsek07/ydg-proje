package com.haccp.audit.repository;

import com.haccp.audit.entity.CorrectiveAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectiveActionRepository extends JpaRepository<CorrectiveAction, Long> {
    List<CorrectiveAction> findByNonConformityId(Long nonConformityId);
}
