package com.pcr.integration.starlims.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class StarLimsQueryAndExportResult {

    private boolean success;
    private String message;

    private StarLimsSqlQueryResult queryResult;
    private StarLimsExportResult exportResult;

    // 新增：把常用信息抬平，方便总流程和前端直接取
    private String fileName;
    private String downloadUrl;
    private String currentUrl;
    private String startTime;
    private String endTime;
    private String pageSnapshot;
    private JsonNode dataset;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public StarLimsSqlQueryResult getQueryResult() {
        return queryResult;
    }

    public StarLimsExportResult getExportResult() {
        return exportResult;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getPageSnapshot() {
        return pageSnapshot;
    }

    public JsonNode getDataset() {
        return dataset;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setQueryResult(StarLimsSqlQueryResult queryResult) {
        this.queryResult = queryResult;
    }

    public void setExportResult(StarLimsExportResult exportResult) {
        this.exportResult = exportResult;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setPageSnapshot(String pageSnapshot) {
        this.pageSnapshot = pageSnapshot;
    }

    public void setDataset(JsonNode dataset) {
        this.dataset = dataset;
    }
}
