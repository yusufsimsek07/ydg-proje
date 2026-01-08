package com.haccp.audit.service;

import com.haccp.audit.entity.NonConformity;
import com.haccp.audit.repository.AuditRepository;
import com.haccp.audit.repository.ChecklistItemRepository;
import com.haccp.audit.repository.NonConformityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NonConformityService {
    @Autowired
    private NonConformityRepository nonConformityRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private ChecklistItemRepository itemRepository;

    public NonConformity createNonConformity(Long auditId, Long itemId, NonConformity.Severity severity, String description) {
        var audit = auditRepository.findById(auditId)
            .orElseThrow(() -> new RuntimeException("Audit not found"));
        
        var item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        NonConformity nc = new NonConformity(audit, item, severity, description);
        return nonConformityRepository.save(nc);
    }

    public List<NonConformity> findAll() {
        return nonConformityRepository.findAll();
    }

    public Optional<NonConformity> findById(Long id) {
        return nonConformityRepository.findById(id);
    }

    public NonConformity updateStatus(Long id, NonConformity.NCStatus newStatus) {
        NonConformity nc = nonConformityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("NonConformity not found"));

        if (newStatus == NonConformity.NCStatus.CLOSED) {
            boolean hasDoneCA = nc.getCorrectiveActions().stream()
                .anyMatch(ca -> ca.getStatus() == com.haccp.audit.entity.CorrectiveAction.CAStatus.DONE);
            
            if (!hasDoneCA) {
                throw new IllegalStateException("Cannot close NonConformity: at least one CorrectiveAction must be DONE");
            }
        }

        nc.setStatus(newStatus);
        return nonConformityRepository.save(nc);
    }

    public List<NonConformity> findByAuditId(Long auditId) {
        return nonConformityRepository.findByAuditId(auditId);
    }
}
