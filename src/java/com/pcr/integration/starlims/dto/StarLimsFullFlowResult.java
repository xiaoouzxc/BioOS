package com.pcr.integration.starlims.dto;

public class StarLimsFullFlowResult {
    private boolean success;
    private String message;

    private Object loginResult;
    private SampleReceiveResult receiveResult;
    private StarLimsQueryAndExportResult exportResult;
    private StarLimsSampleImportResult importResult;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(Object loginResult) {
        this.loginResult = loginResult;
    }

    public SampleReceiveResult getReceiveResult() {
        return receiveResult;
    }

    public void setReceiveResult(SampleReceiveResult receiveResult) {
        this.receiveResult = receiveResult;
    }

    public StarLimsQueryAndExportResult getExportResult() {
        return exportResult;
    }

    public void setExportResult(StarLimsQueryAndExportResult exportResult) {
        this.exportResult = exportResult;
    }

    public StarLimsSampleImportResult getImportResult() {
        return importResult;
    }

    public void setImportResult(StarLimsSampleImportResult importResult) {
        this.importResult = importResult;
    }
}
