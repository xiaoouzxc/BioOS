package com.pcr.integration.starlims.dto;

public class StarLimsSqlQueryResult {

    private boolean success;
    private String message;

    private String currentUrl;
    private Integer templateNo;
    private String groupName;

    private String confirmReceiveTime;
    private String startTime;
    private String endTime;

    private boolean templateClicked;
    private boolean groupSelected;
    private boolean startTimeFilled;
    private boolean endTimeFilled;
    private boolean executeClicked;
    private boolean resultLoaded;
    private String actualGroupName;

    private String pageSnapshot;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public Integer getTemplateNo() {
        return templateNo;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getConfirmReceiveTime() {
        return confirmReceiveTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isTemplateClicked() {
        return templateClicked;
    }

    public boolean isGroupSelected() {
        return groupSelected;
    }

    public boolean isStartTimeFilled() {
        return startTimeFilled;
    }

    public boolean isEndTimeFilled() {
        return endTimeFilled;
    }

    public boolean isExecuteClicked() {
        return executeClicked;
    }

    public boolean isResultLoaded() {
        return resultLoaded;
    }

    public String getPageSnapshot() {
        return pageSnapshot;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public void setTemplateNo(Integer templateNo) {
        this.templateNo = templateNo;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setConfirmReceiveTime(String confirmReceiveTime) {
        this.confirmReceiveTime = confirmReceiveTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setTemplateClicked(boolean templateClicked) {
        this.templateClicked = templateClicked;
    }

    public void setGroupSelected(boolean groupSelected) {
        this.groupSelected = groupSelected;
    }

    public void setStartTimeFilled(boolean startTimeFilled) {
        this.startTimeFilled = startTimeFilled;
    }

    public void setEndTimeFilled(boolean endTimeFilled) {
        this.endTimeFilled = endTimeFilled;
    }

    public void setExecuteClicked(boolean executeClicked) {
        this.executeClicked = executeClicked;
    }

    public void setResultLoaded(boolean resultLoaded) {
        this.resultLoaded = resultLoaded;
    }

    public void setPageSnapshot(String pageSnapshot) {
        this.pageSnapshot = pageSnapshot;
    }

	public String getActualGroupName() {
		return actualGroupName;
	}

	public void setActualGroupName(String actualGroupName) {
		this.actualGroupName = actualGroupName;
	}
}