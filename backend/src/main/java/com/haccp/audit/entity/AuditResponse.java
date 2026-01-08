package com.haccp.audit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "audit_responses")
public class AuditResponse {
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
    private ResponseResult result = ResponseResult.NA;

    @Column(columnDefinition = "TEXT")
    private String comment;

    public enum ResponseResult {
        PASS, FAIL, NA
    }

    public AuditResponse() {}

    public AuditResponse(Audit audit, ChecklistItem item) {
        this.audit = audit;
        this.item = item;
        this.result = ResponseResult.NA;
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

    public ResponseResult getResult() {
        return result;
    }

    public void setResult(ResponseResult result) {
        this.result = result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
