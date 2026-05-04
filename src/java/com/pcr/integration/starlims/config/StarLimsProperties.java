package com.pcr.integration.starlims.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "starlims")
public class StarLimsProperties {

    private String baseUrl;
    private String loginUrl;
    private String username;
    private String password;
    private String exportDir = "D:/starlims-downloads";
    private String defaultGroupName = "内毒素组";
    private Integer defaultTemplateNo = 196;
    private String defaultReceiveStep = "ReceiveCheck";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExportDir() {
        return exportDir;
    }

    public void setExportDir(String exportDir) {
        this.exportDir = exportDir;
    }

    public String getDefaultGroupName() {
        return defaultGroupName;
    }

    public void setDefaultGroupName(String defaultGroupName) {
        this.defaultGroupName = defaultGroupName;
    }

    public Integer getDefaultTemplateNo() {
        return defaultTemplateNo;
    }

    public void setDefaultTemplateNo(Integer defaultTemplateNo) {
        this.defaultTemplateNo = defaultTemplateNo;
    }

    public String getDefaultReceiveStep() {
        return defaultReceiveStep;
    }

    public void setDefaultReceiveStep(String defaultReceiveStep) {
        this.defaultReceiveStep = defaultReceiveStep;
    }
}
