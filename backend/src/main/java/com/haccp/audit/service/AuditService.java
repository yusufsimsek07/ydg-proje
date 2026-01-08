package com.haccp.audit.service;

import com.haccp.audit.entity.*;
import com.haccp.audit.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuditService {
    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChecklistTemplateRepository templateRepository;

    @Autowired
    private ChecklistItemRepository itemRepository;

    @Autowired
    private AuditResponseRepository responseRepository;

    public Audit createAudit(Long facilityId, LocalDate auditDate, String username) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ChecklistTemplate template = templateRepository.findByActiveTrue()
            .orElseThrow(() -> new RuntimeException("No active checklist template found"));

        Audit audit = new Audit(facility, auditDate, user);
        audit.setStatus(Audit.AuditStatus.DRAFT);
        audit = auditRepository.save(audit);

        List<ChecklistItem> items = itemRepository.findByTemplateId(template.getId());
        for (ChecklistItem item : items) {
            AuditResponse response = new AuditResponse(audit, item);
            audit.getResponses().add(response);
        }
        
        return auditRepository.save(audit);
    }

    public List<Audit> findByCreatedBy(String username) {
        return auditRepository.findByCreatedByUsernameOrderByAuditDateDesc(username);
    }

    public Optional<Audit> findById(Long id) {
        return auditRepository.findById(id);
    }

    public Audit updateResponse(Long auditId, Long itemId, AuditResponse.ResponseResult result, String comment) {
        Audit audit = auditRepository.findById(auditId)
            .orElseThrow(() -> new RuntimeException("Audit not found"));

        AuditResponse response = audit.getResponses().stream()
            .filter(r -> r.getItem().getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Response not found"));

        response.setResult(result);
        response.setComment(comment);

        if (audit.getStatus() == Audit.AuditStatus.DRAFT) {
            audit.setStatus(Audit.AuditStatus.IN_PROGRESS);
        }

        return auditRepository.save(audit);
    }

    public Audit completeAudit(Long auditId) {
        Audit audit = auditRepository.findById(auditId)
            .orElseThrow(() -> new RuntimeException("Audit not found"));

        audit.setStatus(Audit.AuditStatus.COMPLETED);
        return auditRepository.save(audit);
    }

    public Audit save(Audit audit) {
        return auditRepository.save(audit);
    }
}
