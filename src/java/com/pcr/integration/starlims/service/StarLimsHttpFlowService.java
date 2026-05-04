package com.pcr.integration.starlims.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pcr.integration.starlims.config.StarLimsProperties;
import com.pcr.integration.starlims.dto.SampleReceiveResult;
import com.pcr.integration.starlims.dto.StarLimsExportResult;
import com.pcr.integration.starlims.dto.StarLimsFullFlowRequest;
import com.pcr.integration.starlims.dto.StarLimsFullFlowResult;
import com.pcr.integration.starlims.dto.StarLimsQueryAndExportResult;
import com.pcr.integration.starlims.dto.StarLimsSqlQueryRequest;
import com.pcr.integration.starlims.dto.StarLimsSqlQueryResult;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StarLimsHttpFlowService {

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StarLimsHttpClientService httpClientService;
    private final StarLimsProperties properties;

    public StarLimsHttpFlowService(StarLimsHttpClientService httpClientService, StarLimsProperties properties) {
        this.httpClientService = httpClientService;
        this.properties = properties;
    }

    public Object httpLogin() {
        return httpClientService.loginByHttp();
    }

    public SampleReceiveResult receivePendingSamples() {
        return receivePendingSamples(properties.getDefaultReceiveStep(), properties.getUsername(), null, null);
    }

    public SampleReceiveResult receivePendingSamples(
            String stepCode,
            String username,
            String groupName,
            Integer templateNo) {

        httpClientService.ensureLoggedIn();

        SampleReceiveResult result = new SampleReceiveResult();
        String effectiveStep = isBlank(stepCode) ? properties.getDefaultReceiveStep() : stepCode;
        String effectiveUser = isBlank(username) ? properties.getUsername() : username;
        String effectiveGroup = isBlank(groupName) ? properties.getDefaultGroupName() : groupName;
        Integer effectiveTemplate = templateNo == null ? properties.getDefaultTemplateNo() : templateNo;

        try {
            List<PendingSampleRow> samples = getPendingSamplesInternal(effectiveStep, effectiveUser);
            result.setLeftTableCount(samples.size());
            result.setGroupName(effectiveGroup);
            result.setTemplateNo(effectiveTemplate);
            result.setConfirmReceiveTime(LocalDateTime.now().format(OUTPUT_FORMATTER));
            result.setPageSnapshot("HTTP receive flow");

            if (samples.isEmpty()) {
                result.setSuccess(true);
                result.setRightTableCount(0);
                result.setMessage("没有待接收样品，跳过接收步骤");
                return result;
            }

            String operationNos = samples.stream()
                    .map(PendingSampleRow::operationNo)
                    .filter(value -> !isBlank(value))
                    .collect(Collectors.joining(","));

            JsonNode response = httpClientService.postJson(
                    "SampleManagement.UpdateSampleRecord",
                    new Object[]{"Receive", operationNos, effectiveUser}
            );

            boolean success = response.isArray() && response.size() > 0 && response.get(0).asBoolean(false);
            result.setSuccess(success);
            result.setRightTableCount(success ? samples.size() : 0);
            result.setMessage(success
                    ? "HTTP 确认接收成功，共 " + samples.size() + " 条"
                    : "HTTP 确认接收失败: " + response.toString());
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("HTTP 接收失败: " + e.getMessage());
            result.setGroupName(effectiveGroup);
            result.setTemplateNo(effectiveTemplate);
            result.setConfirmReceiveTime(LocalDateTime.now().format(OUTPUT_FORMATTER));
            result.setPageSnapshot("HTTP receive flow error");
            return result;
        }
    }

    public StarLimsQueryAndExportResult executeSqlQueryAndExportExcel(StarLimsSqlQueryRequest request) {
        httpClientService.ensureLoggedIn();

        StarLimsQueryAndExportResult result = new StarLimsQueryAndExportResult();
        try {
            StarLimsSqlQueryRequest effectiveRequest = normalizeSqlRequest(request, null);

            StarLimsSqlQueryResult queryResult = new StarLimsSqlQueryResult();
            queryResult.setSuccess(true);
            queryResult.setTemplateClicked(true);
            queryResult.setGroupSelected(true);
            queryResult.setStartTimeFilled(true);
            queryResult.setEndTimeFilled(true);
            queryResult.setExecuteClicked(true);
            queryResult.setResultLoaded(true);
            queryResult.setCurrentUrl(httpClientService.getFddBaseUrl()
                    + "/starthtml.lims?FormId=QueryBySqlCommand.MainForm&formargs=%5B%22NORMAL%22%5D");
            queryResult.setTemplateNo(effectiveRequest.getTemplateNo());
            queryResult.setGroupName(effectiveRequest.getGroupName());
            queryResult.setActualGroupName(effectiveRequest.getGroupName());
            queryResult.setConfirmReceiveTime(effectiveRequest.getConfirmReceiveTime());

            TimeRange timeRange = buildTimeRange(effectiveRequest.getConfirmReceiveTime());
            
            System.out.println(timeRange.startTime().toString()+"----"+timeRange.endTime().toString());
            System.out.println("templateNo=" + request.getTemplateNo());
            System.out.println("groupName=" + request.getGroupName());
           
            queryResult.setStartTime(timeRange.startTime());
            queryResult.setEndTime(timeRange.endTime());

            String sqlTemplate = getSqlTemplate(effectiveRequest.getTemplateNo());
            String xml = executeQueryXml(sqlTemplate, effectiveRequest.getGroupName(), timeRange.startTime(), timeRange.endTime());
            
            ObjectMapper mapper = new ObjectMapper();

         // 第一次反序列化（去掉外层引号）
         String cleanXml = mapper.readValue(xml, String.class);

         // 第二次处理 Unicode
         cleanXml = StringEscapeUtils.unescapeJava(cleanXml);
            
            JsonNode dataset = convertDataset(cleanXml);
            

            int rowCount = countRows(dataset);
            queryResult.setMessage("HTTP SQL 查询成功，返回 " + rowCount + " 行");
            queryResult.setPageSnapshot("HTTP query result rows=" + rowCount);

            StarLimsExportResult exportResult = exportExcel(dataset);
            
           
            

            result.setQueryResult(queryResult);
            result.setExportResult(exportResult);
            result.setSuccess(exportResult.isSuccess());
            result.setMessage(exportResult.isSuccess() ? "HTTP 打单成功" : exportResult.getMessage());
            result.setFileName(exportResult.getFileName());
            result.setDownloadUrl(exportResult.getDownloadUrl());
            result.setCurrentUrl(queryResult.getCurrentUrl());
            result.setStartTime(queryResult.getStartTime());
            result.setEndTime(queryResult.getEndTime());
            result.setPageSnapshot(exportResult.getPageSnapshot());
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("HTTP SQL 查询导出失败: " + e.getMessage());
            result.setPageSnapshot("HTTP query/export error");
            return result;
        }
    }
    
    public StarLimsQueryAndExportResult executeSqlQueryAndExportJson(StarLimsSqlQueryRequest request) {
        httpClientService.ensureLoggedIn();

        StarLimsQueryAndExportResult result = new StarLimsQueryAndExportResult();
        try {
            StarLimsSqlQueryRequest effectiveRequest = normalizeSqlRequest(request, null);

            StarLimsSqlQueryResult queryResult = new StarLimsSqlQueryResult();
            queryResult.setSuccess(true);
            queryResult.setTemplateClicked(true);
            queryResult.setGroupSelected(true);
            queryResult.setStartTimeFilled(true);
            queryResult.setEndTimeFilled(true);
            queryResult.setExecuteClicked(true);
            queryResult.setResultLoaded(true);
            queryResult.setCurrentUrl(httpClientService.getFddBaseUrl()
                    + "/starthtml.lims?FormId=QueryBySqlCommand.MainForm&formargs=%5B%22NORMAL%22%5D");
            queryResult.setTemplateNo(effectiveRequest.getTemplateNo());
            queryResult.setGroupName(effectiveRequest.getGroupName());
            queryResult.setActualGroupName(effectiveRequest.getGroupName());
            queryResult.setConfirmReceiveTime(effectiveRequest.getConfirmReceiveTime());

            TimeRange timeRange = buildTimeRange(effectiveRequest.getConfirmReceiveTime());
            
            System.out.println(timeRange.startTime().toString()+"----"+timeRange.endTime().toString());
            System.out.println("templateNo=" + request.getTemplateNo());
            System.out.println("groupName=" + request.getGroupName());
           
            queryResult.setStartTime(timeRange.startTime());
            queryResult.setEndTime(timeRange.endTime());

            String sqlTemplate = getSqlTemplate(effectiveRequest.getTemplateNo());
            String xml = executeQueryXml(sqlTemplate, effectiveRequest.getGroupName(), timeRange.startTime(), timeRange.endTime());
            
            ObjectMapper mapper = new ObjectMapper();

         // 第一次反序列化（去掉外层引号）
         String cleanXml = mapper.readValue(xml, String.class);

         // 第二次处理 Unicode
         cleanXml = StringEscapeUtils.unescapeJava(cleanXml);
            
            JsonNode dataset = convertDataset(cleanXml);
            

            int rowCount = countRows(dataset);
            queryResult.setMessage("HTTP SQL 查询成功，返回 " + rowCount + " 行");
            queryResult.setPageSnapshot("HTTP query result rows=" + rowCount);

           // StarLimsExportResult exportResult = exportExcel(dataset);
            
           
            

            result.setQueryResult(queryResult);
            result.setSuccess(true);
            result.setMessage("HTTP SQL 查询成功");

            // 🔥 关键：把数据塞进去
            result.setDataset(dataset);

            
           // result.setExportResult(exportResult);
           // result.setSuccess(exportResult.isSuccess());
           // result.setMessage(exportResult.isSuccess() ? "HTTP 打单成功" : exportResult.getMessage());
            //result.setFileName(exportResult.getFileName());
           // result.setDownloadUrl(exportResult.getDownloadUrl());
            result.setCurrentUrl(queryResult.getCurrentUrl());
            result.setStartTime(queryResult.getStartTime());
            result.setEndTime(queryResult.getEndTime());
           // result.setPageSnapshot(exportResult.getPageSnapshot());
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("HTTP SQL 查询导出失败: " + e.getMessage());
            result.setPageSnapshot("HTTP query/export error");
            return result;
        }
    }

    public StarLimsFullFlowResult executePrintTicketFlow(StarLimsFullFlowRequest request) {
        StarLimsFullFlowResult result = new StarLimsFullFlowResult();
        try {
            result.setLoginResult(httpLogin());

            StarLimsSqlQueryRequest sqlRequest = request == null ? null : request.getSqlRequest();
            SampleReceiveResult receiveResult = receivePendingSamples(
                    properties.getDefaultReceiveStep(),
                    properties.getUsername(),
                    sqlRequest == null ? null : sqlRequest.getGroupName(),
                    sqlRequest == null ? null : sqlRequest.getTemplateNo()
            );
            result.setReceiveResult(receiveResult);

            StarLimsQueryAndExportResult exportResult = executeSqlQueryAndExportExcel(
                    normalizeSqlRequest(sqlRequest, receiveResult)
            );
            result.setExportResult(exportResult);
            result.setSuccess(exportResult.isSuccess());
            result.setMessage(exportResult.isSuccess() ? "HTTP 打单流程执行成功" : exportResult.getMessage());
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("HTTP 打单流程执行失败: " + e.getMessage());
            return result;
        }
    }
    
    public StarLimsFullFlowResult executePrintJsonTicketFlow(StarLimsFullFlowRequest request) {
        StarLimsFullFlowResult result = new StarLimsFullFlowResult();
        try {
            result.setLoginResult(httpLogin());

            StarLimsSqlQueryRequest sqlRequest = request == null ? null : request.getSqlRequest();
            SampleReceiveResult receiveResult = receivePendingSamples(
                    properties.getDefaultReceiveStep(),
                    properties.getUsername(),
                    sqlRequest == null ? null : sqlRequest.getGroupName(),
                    sqlRequest == null ? null : sqlRequest.getTemplateNo()
            );
            result.setReceiveResult(receiveResult);

            StarLimsQueryAndExportResult exportResult = executeSqlQueryAndExportJson(
                    normalizeSqlRequest(sqlRequest, receiveResult)
            );
            result.setExportResult(exportResult);
            result.setSuccess(exportResult.isSuccess());
            result.setMessage(exportResult.isSuccess() ? "HTTP 打单流程执行成功" : exportResult.getMessage());
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("HTTP 打单流程执行失败: " + e.getMessage());
            return result;
        }
    }

    private List<PendingSampleRow> getPendingSamplesInternal(String stepCode, String username) {
        String where = " and rpr.STATUS = ? ";
        String params = "'" + stepCode + "'";

        String finalWhere = where;
        String finalParams = params;
        if ("ReceiveCheck".equals(stepCode)) {
            finalWhere = where + " and rpr.PROVIDE_PERSON = ? ";
            finalParams = params + ", '" + username + "'";
        }

        JsonNode result = httpClientService.postJson(
                "Runtime_Support.GetDataAsJson",
                new Object[]{"SampleManagement.dgPendingSend", new Object[]{stepCode, finalWhere, finalParams}}
        );

        List<PendingSampleRow> rows = new ArrayList<>();
        JsonNode dataRows = result.path("Tables").path(0).path("Rows");
        if (!dataRows.isArray()) {
            return rows;
        }

        for (JsonNode row : dataRows) {
            rows.add(new PendingSampleRow(
                    row.path("OPERATION_NO").asText(""),
                    row.path("SERVGRP").asText("")
            ));
        }
        return rows;
    }

    private String getSqlTemplate(Integer templateNo) {
        String path = "RUNTIME_SUPPORT.GetData.lims?Provider=QueryBySqlCommand.dsSqlCommand&Type=json&p1=" + templateNo;
        JsonNode result = httpClientService.getJson(path);
        return result.path("Tables").path(0).path("Rows").path(0).path("SQLCONTENT").asText("");
    }

    private String executeQueryXml(String sqlTemplate, String groupName, String startTime, String endTime) {
        String response = httpClientService.postText(
                "QueryBySqlCommand.ExecuteSql",
                new Object[]{sqlTemplate, "", new Object[]{groupName, startTime, endTime, ""}}
        );
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("ExecuteSql 返回为空");
        }

        // 判断是否真正报错
        if (response.contains("<Error>")) {
            throw new RuntimeException("ExecuteSql 业务异常: " + response);
        }

        // ✔ 正常 XML，直接返回
        return response;
        
    }

    private JsonNode convertDataset(String xmlData) {
        return httpClientService.postJson("Enterprise_Server.DataSetSupport.DSFromString", new Object[]{xmlData});
    }

    private StarLimsExportResult exportExcel(JsonNode datasetJson) {
        StarLimsExportResult result = new StarLimsExportResult();
        ObjectMapper mapper = httpClientService.getObjectMapper();

        try {
            ObjectNode exportData = mapper.createObjectNode();
            exportData.put("$ClassName", "System.Data.DataSet");
            exportData.put("$DataSet", "msdata:IsDataSet=\"true\"");

            ArrayNode convertedTables = mapper.createArrayNode();
            ArrayNode columnsList = mapper.createArrayNode();

            JsonNode tables = datasetJson.path("Tables");
            if (tables.isArray()) {
                for (JsonNode table : tables) {
                    ObjectNode convertedTable = mapper.createObjectNode();
                    convertedTable.put("TableName", table.path("TableName").asText(""));

                    ArrayNode newColumns = mapper.createArrayNode();
                    JsonNode columns = table.path("Columns");
                    if (columns.isArray()) {
                        for (JsonNode col : columns) {
                            String columnName = col.path("ColumnName").asText("");

                            ObjectNode newCol = mapper.createObjectNode();
                            newCol.put("ColumnName", columnName);
                            newCol.putNull("DefaultValue");
                            newCol.put("DataType", col.path("DataType").asText("System.String"));
                            newCol.put("MaxLength", col.path("MaxLength").asInt(-1));
                            newCol.put("AllowDBNull", col.path("AllowDBNull").asBoolean(true));
                            newCol.put("DateTimeMode", "Local");
                            newCol.put("ReadOnly", col.path("ReadOnly").asBoolean(false));
                            newCol.put("Unique", col.path("Unique").asBoolean(false));
                            newCol.put("DecimalScale", -1);
                            newCol.put("DecimalPrecision", -1);

                            ObjectNode ext = mapper.createObjectNode();
                            ext.put("AllowDBNull", String.valueOf(col.path("AllowDBNull").asBoolean(true)));
                            ext.put("ReadOnly", String.valueOf(col.path("ReadOnly").asBoolean(false)));
                            ext.put("Unique", String.valueOf(col.path("Unique").asBoolean(false)));
                            newCol.set("ExtendedProperties", ext);
                            newColumns.add(newCol);

                            ObjectNode colDef = mapper.createObjectNode();
                            colDef.put("Caption", columnName);
                            colDef.put("DataMember", columnName);
                            columnsList.add(colDef);
                        }
                    }

                    convertedTable.set("Columns", newColumns);
                    convertedTable.set("Rows", table.path("Rows"));
                    convertedTables.add(convertedTable);
                }
            }

            exportData.set("Tables", convertedTables);

            String payload = mapper.writeValueAsString(exportData);
            JsonNode exportResponse = httpClientService.postJson(
                    "OfficeAutomation.ExportToExcel",
                    new Object[]{payload, columnsList, null}
            );

            String serverPath = extractServerPath(exportResponse);
            if (isBlank(serverPath) || !serverPath.toLowerCase().contains(".xlsx")) {
                throw new RuntimeException("ExportToExcel 返回异常: " + exportResponse);
            }

            String fileName = extractFileName(serverPath);
            saveExcel(serverPath, fileName);

            result.setSuccess(true);
            result.setFileName(fileName);
            result.setDownloadUrl("/api/starlims/download-export?fileName=" + httpClientService.urlEncode(fileName));
            result.setMessage("HTTP 导出成功");
            result.setPageSnapshot("HTTP export success");
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("HTTP 导出失败: " + e.getMessage());
            result.setPageSnapshot("HTTP export error");
            return result;
        }
    }

    private void saveExcel(String serverPath, String fileName) throws IOException {
        Path exportDir = Paths.get(properties.getExportDir());
        Files.createDirectories(exportDir);
        Path localFile = exportDir.resolve(fileName);

        List<String> urls = List.of(
                httpClientService.getFddBaseUrl()
                        + "/RUNTIME_SUPPORT.GetFile.lims?Provider=Enterprise_Utilities.EchoFile&p1="
                        + httpClientService.urlEncode(serverPath),
                httpClientService.getFddBaseUrl() + "/WorkPath/Temp/" + fileName
        );

        for (String url : urls) {
            try {
                byte[] content = httpClientService.getBytesAbsolute(url);
                if (content.length > 100) {
                    Files.write(localFile, content);
                    return;
                }
            } catch (Exception ignored) {
                // try the next download path
            }
        }

        throw new IOException("无法下载导出的 Excel 文件: " + fileName);
    }

    private int countRows(JsonNode dataset) {
        JsonNode rows = dataset.path("Tables").path(0).path("Rows");
        return rows.isArray() ? rows.size() : 0;
    }

    private StarLimsSqlQueryRequest normalizeSqlRequest(StarLimsSqlQueryRequest request, SampleReceiveResult receiveResult) {
        StarLimsSqlQueryRequest effective = request == null ? new StarLimsSqlQueryRequest() : request;

        if (isBlank(effective.getConfirmReceiveTime())) {
            String receiveTime = receiveResult == null ? null : receiveResult.getConfirmReceiveTime();
            effective.setConfirmReceiveTime(isBlank(receiveTime)
                    ? LocalDateTime.now().format(OUTPUT_FORMATTER)
                    : receiveTime);
        }

        if (isBlank(effective.getGroupName())) {
            String groupName = receiveResult == null ? null : receiveResult.getGroupName();
            effective.setGroupName(isBlank(groupName) ? properties.getDefaultGroupName() : groupName);
        }

        if (effective.getTemplateNo() == null) {
            Integer templateNo = receiveResult == null ? null : receiveResult.getTemplateNo();
            effective.setTemplateNo(templateNo == null ? properties.getDefaultTemplateNo() : templateNo);
        }

        return effective;
    }

    private TimeRange buildTimeRange(String confirmReceiveTime) {
        LocalDateTime base = parseConfirmReceiveTime(confirmReceiveTime);
        return new TimeRange(
                OUTPUT_FORMATTER.format(base.minusMinutes(1)),
                OUTPUT_FORMATTER.format(base.plusMinutes(1))
        );
    }

    private LocalDateTime parseConfirmReceiveTime(String confirmReceiveTime) {
        if (isBlank(confirmReceiveTime)) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(confirmReceiveTime, INPUT_FORMATTER);
    }

    private String extractServerPath(JsonNode exportResponse) throws JsonProcessingException {
        if (exportResponse == null || exportResponse.isNull()) {
            return "";
        }
        if (exportResponse.isTextual()) {
            return exportResponse.asText("");
        }
        if (exportResponse.isArray() && exportResponse.size() > 0) {
            JsonNode first = exportResponse.get(0);
            if (first != null && first.isTextual()) {
                return first.asText("");
            }
        }
        if (exportResponse.has("path")) {
            return exportResponse.path("path").asText("");
        }
        return httpClientService.getObjectMapper().writeValueAsString(exportResponse);
    }

    private static String extractFileName(String serverPath) {
        String normalized = serverPath.replace('/', '\\');
        int idx = normalized.lastIndexOf('\\');
        return idx >= 0 ? normalized.substring(idx + 1) : normalized;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String abbreviate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private record PendingSampleRow(String operationNo, String groupName) {
    }

    private record TimeRange(String startTime, String endTime) {
    }
}
