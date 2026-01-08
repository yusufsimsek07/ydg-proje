package com.haccp.audit.service;

import com.haccp.audit.entity.Audit;
import com.haccp.audit.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ReportService {
    @Autowired
    private AuditRepository auditRepository;

    public Optional<Audit> getAuditReport(Long auditId) {
        return auditRepository.findById(auditId);
    }
}
