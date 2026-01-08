package com.haccp.audit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "non_conformities")
public class NonConformity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id", nullable = false)
    private Audit audit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ChecklistItem item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity = Severity.LOW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NCStatus status = NCStatus.OPEN;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "nonConformity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CorrectiveAction> correctiveActions = new ArrayList<>();

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum NCStatus {
        OPEN, IN_PROGRESS, CLOSED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public NonConformity() {}

    public NonConformity(Audit audit, ChecklistItem item, Severity severity, String description) {
        this.audit = audit;
        this.item = item;
        this.severity = severity;
        this.description = description;
        this.status = NCStatus.OPEN;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public ChecklistItem getItem() {
        return item;
    }

    public void setItem(ChecklistItem item) {
        this.item = item;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public NCStatus getStatus() {
        return status;
    }

    public void setStatus(NCStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CorrectiveAction> getCorrectiveActions() {
        return correctiveActions;
    }

    public void setCorrectiveActions(List<CorrectiveAction> correctiveActions) {
        this.correctiveActions = correctiveActions;
    }
}
