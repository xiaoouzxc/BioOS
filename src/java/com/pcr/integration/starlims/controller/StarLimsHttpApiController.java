package com.pcr.integration.starlims.controller;

import com.pcr.integration.starlims.dto.SampleReceiveResult;
import com.pcr.integration.starlims.dto.StarLimsFullFlowRequest;
import com.pcr.integration.starlims.dto.StarLimsFullFlowResult;
import com.pcr.integration.starlims.dto.StarLimsQueryAndExportResult;
import com.pcr.integration.starlims.dto.StarLimsSqlQueryRequest;
import com.pcr.integration.starlims.service.StarLimsAutoImportService;
import com.pcr.integration.starlims.service.StarLimsHttpFlowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/starlims")
public class StarLimsHttpApiController {

    private final StarLimsHttpFlowService httpFlowService;
    private final StarLimsAutoImportService autoImportService;

    public StarLimsHttpApiController(StarLimsHttpFlowService httpFlowService,
                                     StarLimsAutoImportService autoImportService) {
        this.httpFlowService = httpFlowService;
        this.autoImportService = autoImportService;
    }

    @GetMapping("/http-login")
    public Object httpLogin() {
        return httpFlowService.httpLogin();
    }

    @GetMapping("/http-receive-all")
    public SampleReceiveResult httpReceiveAll() {
        return httpFlowService.receivePendingSamples();
    }

    @PostMapping("/http-query-export-sql-excel")
    public StarLimsQueryAndExportResult httpQueryExportSqlExcel(@RequestBody StarLimsSqlQueryRequest request) {
        return httpFlowService.executeSqlQueryAndExportExcel(request);
    }

    @PostMapping("/http-print-ticket")
    public StarLimsFullFlowResult httpPrintTicket(@RequestBody(required = false) StarLimsFullFlowRequest request) {
        return httpFlowService.executePrintTicketFlow(request);
    }

    @PostMapping("/http-print-Json-ticket")
    public StarLimsFullFlowResult httpPrintJsonTicket(@RequestBody(required = false) StarLimsFullFlowRequest request) {
        return autoImportService.executeOnce(request, "api");
    }

    @GetMapping("/auto-import/status")
    public Map<String, Object> autoImportStatus() {
        return autoImportService.status();
    }

    @PostMapping("/auto-import/config")
    public Map<String, Object> updateAutoImportConfig(@RequestBody Map<String, Object> body) {
        Integer intervalMinutes = null;
        Boolean paused = null;
        if (body != null && body.get("intervalMinutes") != null) {
            intervalMinutes = Number.class.isInstance(body.get("intervalMinutes"))
                    ? ((Number) body.get("intervalMinutes")).intValue()
                    : Integer.valueOf(String.valueOf(body.get("intervalMinutes")));
        }
        if (body != null && body.get("paused") != null) {
            paused = Boolean.valueOf(String.valueOf(body.get("paused")));
        }
        return autoImportService.updateConfig(intervalMinutes, paused);
    }

    @PostMapping("/auto-import/run")
    public StarLimsFullFlowResult runAutoImportNow() {
        return autoImportService.executeOnce(null, "manual");
    }
}
