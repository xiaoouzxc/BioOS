package com.pcr.integration.starlims.dto;

public class StarLimsSqlQueryRequest {

    /**
     * 样品确认接收时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String confirmReceiveTime;

    /**
     * 检测组别，默认：饲料微生物组
     */
    private String groupName = "饲料微生物组";

    /**
     * 模板编号，默认：196
     */
    private Integer templateNo = 196;

    public String getConfirmReceiveTime() {
        return confirmReceiveTime;
    }

    public void setConfirmReceiveTime(String confirmReceiveTime) {
        this.confirmReceiveTime = confirmReceiveTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(Integer templateNo) {
        this.templateNo = templateNo;
    }
}