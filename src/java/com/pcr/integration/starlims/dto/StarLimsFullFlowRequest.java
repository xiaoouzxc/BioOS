package com.pcr.integration.starlims.dto;

public class StarLimsFullFlowRequest {
    private String formId;
    private String formargs;
    private StarLimsSqlQueryRequest sqlRequest;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormargs() {
        return formargs;
    }

    public void setFormargs(String formargs) {
        this.formargs = formargs;
    }

    public StarLimsSqlQueryRequest getSqlRequest() {
        return sqlRequest;
    }

    public void setSqlRequest(StarLimsSqlQueryRequest sqlRequest) {
        this.sqlRequest = sqlRequest;
    }
}