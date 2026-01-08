package com.haccp.audit.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "corrective_actions")
public class CorrectiveAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "non_conformity_id", nullable = false)
    private NonConformity nonConformity;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String actionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CAStatus status = CAStatus.OPEN;

    private LocalDateTime closedAt;

    public enum CAStatus {
        OPEN, DONE
    }

    public CorrectiveAction() {}

    public CorrectiveAction(NonConformity nonConformity, String ownerName, LocalDate dueDate, String actionText) {
        this.nonConformity = nonConformity;
        this.ownerName = ownerName;
        this.dueDate = dueDate;
        this.actionText = actionText;
        this.status = CAStatus.OPEN;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NonConformity getNonConformity() {
        return nonConformity;
    }

    public void setNonConformity(NonConformity nonConformity) {
        this.nonConformity = nonConformity;
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

    public CAStatus getStatus() {
        return status;
    }

    public void setStatus(CAStatus status) {
        this.status = status;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
