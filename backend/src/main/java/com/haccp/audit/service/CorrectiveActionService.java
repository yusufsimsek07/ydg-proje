package com.haccp.audit.service;

import com.haccp.audit.entity.CorrectiveAction;
import com.haccp.audit.repository.CorrectiveActionRepository;
import com.haccp.audit.repository.NonConformityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CorrectiveActionService {
    @Autowired
    private CorrectiveActionRepository correctiveActionRepository;

    @Autowired
    private NonConformityRepository nonConformityRepository;

    public CorrectiveAction createCorrectiveAction(Long nonConformityId, String ownerName, LocalDate dueDate, String actionText) {
        var nc = nonConformityRepository.findById(nonConformityId)
            .orElseThrow(() -> new RuntimeException("NonConformity not found"));

        CorrectiveAction ca = new CorrectiveAction(nc, ownerName, dueDate, actionText);
        return correctiveActionRepository.save(ca);
    }

    public List<CorrectiveAction> findByNonConformityId(Long nonConformityId) {
        return correctiveActionRepository.findByNonConformityId(nonConformityId);
    }

    public Optional<CorrectiveAction> findById(Long id) {
        return correctiveActionRepository.findById(id);
    }

    public CorrectiveAction markAsDone(Long id) {
        CorrectiveAction ca = correctiveActionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("CorrectiveAction not found"));

        ca.setStatus(CorrectiveAction.CAStatus.DONE);
        ca.setClosedAt(LocalDateTime.now());
        return correctiveActionRepository.save(ca);
    }

    public List<CorrectiveAction> findAll() {
        return correctiveActionRepository.findAll();
    }
}
