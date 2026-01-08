package com.haccp.audit.dto;

import com.haccp.audit.entity.AuditResponse;
import jakarta.validation.constraints.NotNull;

public class ResponseUpdateDTO {
    @NotNull
    private Long itemId;

    @NotNull
    private AuditResponse.ResponseResult result;

    private String comment;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public AuditResponse.ResponseResult getResult() {
        return result;
    }

    public void setResult(AuditResponse.ResponseResult result) {
        this.result = result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
