package com.haccp.audit.dto;

import com.haccp.audit.entity.NonConformity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NonConformityCreateDTO {
    @NotNull
    private Long auditId;

    @NotNull
    private Long itemId;

    @NotNull
    private NonConformity.Severity severity;

    @NotBlank
    private String description;

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public NonConformity.Severity getSeverity() {
        return severity;
    }

    public void setSeverity(NonConformity.Severity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
