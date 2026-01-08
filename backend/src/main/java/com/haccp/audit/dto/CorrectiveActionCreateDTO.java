package com.haccp.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CorrectiveActionCreateDTO {
    @NotNull
    private Long nonConformityId;

    @NotBlank
    private String ownerName;

    @NotNull
    private LocalDate dueDate;

    @NotBlank
    private String actionText;

    public Long getNonConformityId() {
        return nonConformityId;
    }

    public void setNonConformityId(Long nonConformityId) {
        this.nonConformityId = nonConformityId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }
}
