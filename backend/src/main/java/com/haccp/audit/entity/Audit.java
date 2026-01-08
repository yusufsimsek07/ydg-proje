package com.haccp.audit.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "audits")
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(nullable = false)
    private LocalDate auditDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditStatus status = AuditStatus.DRAFT;

    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuditResponse> responses = new ArrayList<>();

    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NonConformity> nonConformities = new ArrayList<>();

    public enum AuditStatus {
        DRAFT, IN_PROGRESS, COMPLETED
    }

    public Audit() {}

    public Audit(Facility facility, LocalDate auditDate, User createdBy) {
        this.facility = facility;
        this.auditDate = auditDate;
        this.createdBy = createdBy;
        this.status = AuditStatus.DRAFT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public LocalDate getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDate auditDate) {
        this.auditDate = auditDate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public AuditStatus getStatus() {
        return status;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public List<AuditResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<AuditResponse> responses) {
        this.responses = responses;
    }

    public List<NonConformity> getNonConformities() {
        return nonConformities;
    }

    public void setNonConformities(List<NonConformity> nonConformities) {
        this.nonConformities = nonConformities;
    }
}
