package com.pcr.integration.starlims.dto;



public class SampleReceiveResult {

    private boolean success;
    private String message;

    private String confirmReceiveTime;
    private Integer leftTableCount;
    private Integer rightTableCount;
    private String pageSnapshot;

    // 新增
    private String groupName;
    private Integer templateNo;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public String getConfirmReceiveTime() {
        return confirmReceiveTime;
    }

    public Integer getLeftTableCount() {
        return leftTableCount;
    }

    public Integer getRightTableCount() {
        return rightTableCount;
    }

    public String getPageSnapshot() {
        return pageSnapshot;
    }

    public String getGroupName() {
        return groupName;
    }

    public Integer getTemplateNo() {
        return templateNo;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setConfirmReceiveTime(String confirmReceiveTime) {
        this.confirmReceiveTime = confirmReceiveTime;
    }

    public void setLeftTableCount(Integer leftTableCount) {
        this.leftTableCount = leftTableCount;
    }

    public void setRightTableCount(Integer rightTableCount) {
        this.rightTableCount = rightTableCount;
    }

    public void setPageSnapshot(String pageSnapshot) {
        this.pageSnapshot = pageSnapshot;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setTemplateNo(Integer templateNo) {
        this.templateNo = templateNo;
    }
}