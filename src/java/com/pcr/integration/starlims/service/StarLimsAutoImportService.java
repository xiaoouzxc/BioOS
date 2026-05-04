package com.pcr.integration.starlims.service;

import com.pcr.integration.starlims.dto.StarLimsFullFlowRequest;
import com.pcr.integration.starlims.dto.StarLimsFullFlowResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StarLimsAutoImportService {

    private static final ZoneId DISPLAY_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final long MANUAL_COOLDOWN_MS = 5 * 60 * 1000L;

    private final StarLimsHttpFlowService httpFlowService;
    private final StarLimsSampleImportService sampleImportService;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong manualCooldownUntil = new AtomicLong(0L);

    private volatile int intervalMinutes = 30;
    private volatile boolean paused = false;
    private volatile long nextRunAt = System.currentTimeMillis() + intervalMillis();
    private volatile long lastRunAt = 0L;
    private volatile String lastTrigger = "";
    private volatile String lastMessage = "等待首次执行";
    private volatile boolean lastSuccess = true;

    public StarLimsAutoImportService(StarLimsHttpFlowService httpFlowService,
                                     StarLimsSampleImportService sampleImportService) {
        this.httpFlowService = httpFlowService;
        this.sampleImportService = sampleImportService;
    }

    @Scheduled(fixedDelay = 1000)
    public void runWhenDue() {
        if (paused || running.get() || System.currentTimeMillis() < nextRunAt) {
            return;
        }
        executeOnce(null, "auto");
    }

    public StarLimsFullFlowResult executeOnce(StarLimsFullFlowRequest request, String trigger) {
        String realTrigger = trigger == null ? "manual" : trigger;
        long now = System.currentTimeMillis();

        if ("manual".equals(realTrigger)) {
            long cooldownUntil = manualCooldownUntil.get();

            if (cooldownUntil > now) {
                StarLimsFullFlowResult result = new StarLimsFullFlowResult();
                result.setSuccess(false);
                result.setMessage("手动请求冷却中，请稍后再试");
                return result;
            }

            manualCooldownUntil.set(now + MANUAL_COOLDOWN_MS);
        }

        if (!running.compareAndSet(false, true)) {
            StarLimsFullFlowResult result = new StarLimsFullFlowResult();
            result.setSuccess(false);
            result.setMessage("LIMS 自动导入任务正在执行，请稍后再试");
            return result;
        }

        lastTrigger = realTrigger;

        try {
            StarLimsFullFlowResult result = httpFlowService.executePrintJsonTicketFlow(request);

            if (result.isSuccess()
                    && result.getExportResult() != null
                    && result.getExportResult().getDataset() != null) {
                result.setImportResult(sampleImportService.importDataset(result.getExportResult().getDataset()));
            }

            lastSuccess = result.isSuccess();
            lastMessage = buildLastMessage(result);
            return result;

        } catch (Exception e) {
            StarLimsFullFlowResult result = new StarLimsFullFlowResult();
            result.setSuccess(false);
            result.setMessage("LIMS 自动导入失败: " + e.getMessage());
            lastSuccess = false;
            lastMessage = result.getMessage();
            return result;

        } finally {
            lastRunAt = System.currentTimeMillis();
            nextRunAt = lastRunAt + intervalMillis();
            running.set(false);
        }
    }

    public synchronized Map<String, Object> updateConfig(Integer newIntervalMinutes, Boolean newPaused) {
        boolean intervalChanged = false;

        if (newIntervalMinutes != null) {
            int normalized = Math.max(1, Math.min(1440, newIntervalMinutes));
            intervalChanged = normalized != intervalMinutes;
            intervalMinutes = normalized;
        }

        if (newPaused != null) {
            paused = newPaused;
        }

        if (intervalChanged || Boolean.FALSE.equals(newPaused)) {
            nextRunAt = System.currentTimeMillis() + intervalMillis();
        }

        return status();
    }

    public Map<String, Object> status() {
        Map<String, Object> result = new LinkedHashMap<>();

        long now = System.currentTimeMillis();
        long cooldownUntil = manualCooldownUntil.get();
        long cooldownLeft = Math.max(0L, cooldownUntil - now);

        result.put("intervalMinutes", intervalMinutes);
        result.put("paused", paused);
        result.put("running", running.get());
        result.put("nextRunAt", nextRunAt);
        result.put("nextRunTime", formatMillis(nextRunAt));
        result.put("lastRunAt", lastRunAt);
        result.put("lastRunTime", lastRunAt <= 0 ? "" : formatMillis(lastRunAt));
        result.put("lastTrigger", lastTrigger);
        result.put("lastSuccess", lastSuccess);
        result.put("lastMessage", lastMessage);
        result.put("importVersion", sampleImportService.getImportVersion());
        result.put("lastImportTime", sampleImportService.getLastImportTime());

        result.put("manualCooldownUntil", cooldownUntil);
        result.put("manualCooldownLeft", cooldownLeft);

        result.put("serverTime", now);

        return result;
    }

    private long intervalMillis() {
        return intervalMinutes * 60_000L;
    }

    private String buildLastMessage(StarLimsFullFlowResult result) {
        if (result.getImportResult() != null && result.getImportResult().getMessage() != null) {
            return result.getImportResult().getMessage();
        }
        return result.getMessage() == null ? "执行完成" : result.getMessage();
    }

    private static String formatMillis(long millis) {
        return DISPLAY_FORMATTER.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), DISPLAY_ZONE));
    }
}