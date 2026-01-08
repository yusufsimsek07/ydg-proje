package com.haccp.audit.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AuditCreateDTO {
    @NotNull
    private Long facilityId;

    @NotNull
    private LocalDate auditDate;

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public LocalDate getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDate auditDate) {
        this.auditDate = auditDate;
    }
}
