package com.example.demo;

import com.test.service.SampleTaskTable;
import com.test.service.XmlService;
import com.xml.standards.Method;
import com.xml.standards.MethodProceed;
import com.xml.standards.Standard;
import com.xml.standards.TestItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class DashboardController {
    private static final int PARALLEL_PLATE_COUNT = 2;

    private final JdbcTemplate jdbcTemplate;
    private final XmlService xmlService;

    public DashboardController(JdbcTemplate jdbcTemplate, XmlService xmlService) {
        this.jdbcTemplate = jdbcTemplate;
        this.xmlService = xmlService;
    }

    @GetMapping("/api/dashboard/today-batches")
    public Map<String, Object> todayBatches() {
        String tableName = SampleTaskTable.currentYearTableName();
        Map<String, Object> result = new LinkedHashMap<>();

        Integer sampleTotal = jdbcTemplate.queryForObject(
                "select count(distinct `样品短号`) from `" + tableName + "` where date(`传入时间`) = curdate()",
                Integer.class
        );
        Integer projectTotal = jdbcTemplate.queryForObject(
                "select count(*) from `" + tableName + "` where date(`传入时间`) = curdate()",
                Integer.class
        );

        String sql = "select `顺序` as batch, "
                + "count(distinct `样品短号`) as sample_count, "
                + "count(*) as project_count, "
                + "count(distinct case when `done` = 1 then `样品短号` end) as done_count, "
                + "count(distinct case when `复测` is not null then `样品短号` end) as retest_count, "
                + "group_concat(distinct nullif(`位置`, '') order by `位置` separator ', ') as owners "
                + "from `" + tableName + "` "
                + "where date(`传入时间`) = curdate() "
                + "group by `顺序` "
                + "order by `顺序`";

        List<Map<String, Object>> batches = new ArrayList<>();
        jdbcTemplate.query(sql, rs -> {
            int sampleCount = rs.getInt("sample_count");
            int doneCount = rs.getInt("done_count");
            String owners = rs.getString("owners");
            boolean claimed = owners != null && !owners.isBlank();

            Map<String, Object> batch = new LinkedHashMap<>();
            batch.put("batch", rs.getInt("batch"));
            batch.put("sampleCount", sampleCount);
            batch.put("projectCount", rs.getInt("project_count"));
            batch.put("doneCount", doneCount);
            batch.put("retestCount", rs.getInt("retest_count"));
            batch.put("ownerText", claimed ? "已领取 " + owners : "未领取");
            batch.put("actionText", claimed ? "标签可下载" : "待领取");
            batch.put("status", !claimed ? "待领取" : (doneCount >= sampleCount ? "已完成" : "处理中"));
            batches.add(batch);
        });

        result.put("sampleTotal", sampleTotal == null ? 0 : sampleTotal);
        result.put("projectTotal", projectTotal == null ? 0 : projectTotal);
        result.put("batches", batches);
        return result;
    }

    @GetMapping("/api/dashboard/media-usage")
    public Map<String, Object> mediaUsage() {
        String tableName = SampleTaskTable.currentYearTableName();
        List<Standard> standards = xmlService.readXmlData();
        Map<String, MediaUsage> usageByMedium = new LinkedHashMap<>();
        Map<Integer, Map<String, MediaUsage>> usageByBatch = new LinkedHashMap<>();
        Set<String> countedRows = new HashSet<>();
        Set<String> missingItems = new HashSet<>();

        String sql = "select `顺序`, `样品短号`, `样品名称`, `检测项目`, `检测方法` from `" + tableName + "` "
                + "where date(`传入时间`) = curdate() and `复测` is null "
                + "order by `顺序`, `id`";

        jdbcTemplate.query(sql, rs -> {
            int batch = rs.getInt("顺序");
            String sampleNumber = rs.getString("样品短号");
            String sampleName = rs.getString("样品名称");
            String testItem = rs.getString("检测项目");
            String testMethod = rs.getString("检测方法");
            String rowKey = batch + "|" + safe(sampleNumber) + "|" + safe(testItem) + "|" + safe(testMethod);
            if (!countedRows.add(rowKey)) {
                return;
            }
            List<MethodProceed> proceeds = findMatchingProceeds(testMethod, testItem, sampleName, standards);
            boolean hasMedium = false;
            Map<String, MediaUsage> batchUsage = usageByBatch.computeIfAbsent(batch, ignored -> new LinkedHashMap<>());
            Set<String> mediumCountedInRow = new HashSet<>();
            for (MethodProceed proceed : proceeds) {
                String medium = normalizeConfigValue(proceed.getMedium());
                String quantityText = normalizeConfigValue(proceed.getQuantity());
                if (medium == null || quantityText == null) {
                    continue;
                }
                if (!mediumCountedInRow.add(medium)) {
                    continue;
                }
                hasMedium = true;
                int dilutionCount = parseDilutionCount(proceed.getDilution());
                double quantity = parseQuantity(quantityText);
                if (!Double.isNaN(quantity)) {
                    quantity = quantity * dilutionCount * PARALLEL_PLATE_COUNT;
                }
                addUsage(usageByMedium, medium, quantity, quantityText);
                addUsage(batchUsage, medium, quantity, quantityText);
            }
            if (!hasMedium) {
                missingItems.add(safe(testItem).isBlank() ? "未命名检测项目" : testItem);
            }
        });

        List<Map<String, Object>> totals = new ArrayList<>();
        for (MediaUsage usage : usageByMedium.values()) {
            totals.add(usage.toMap());
        }

        List<Map<String, Object>> batches = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, MediaUsage>> entry : usageByBatch.entrySet()) {
            Map<String, Object> batch = new LinkedHashMap<>();
            List<Map<String, Object>> usages = new ArrayList<>();
            for (MediaUsage usage : entry.getValue().values()) {
                usages.add(usage.toMap());
            }
            batch.put("batch", entry.getKey());
            batch.put("items", usages);
            batches.add(batch);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totals", totals);
        result.put("batches", batches);
        result.put("missingItems", new ArrayList<>(missingItems));
        return result;
    }

    private void addUsage(Map<String, MediaUsage> usageMap, String medium, double quantity, String quantityText) {
        MediaUsage usage = usageMap.computeIfAbsent(medium, MediaUsage::new);
        usage.count++;
        usage.unitQuantityText = quantityText;
        if (!Double.isNaN(quantity)) {
            usage.totalQuantity += quantity;
        } else {
            usage.nonNumericQuantityText = quantityText;
        }
    }

    private List<MethodProceed> findMatchingProceeds(String testMethod, String testItem, String sampleName, List<Standard> standards) {
        List<MethodProceed> proceeds = new ArrayList<>();
        if (testMethod == null || testItem == null) {
            return proceeds;
        }
        for (Standard standard : standards) {
            if (standard == null || standard.getStandardNumber() == null || !testMethod.contains(standard.getStandardNumber())) {
                continue;
            }
            for (TestItem standardItem : standard.getTestItem()) {
                if (standardItem == null || standardItem.getTestItem() == null) {
                    continue;
                }
                if (!testItem.contains(standardItem.getTestItem()) && !standardItem.getTestItem().contains(testItem)) {
                    continue;
                }
                for (Method method : standardItem.getMethod()) {
                    if (method == null || method.getMethod() == null) {
                        continue;
                    }
                    if (!"/".equals(method.getMethod()) && !testMethod.contains(method.getMethod())) {
                        continue;
                    }
                    List<MethodProceed> methodProceeds = method.getMethodProceed();
                    MethodProceed defaultProceed = methodProceeds.isEmpty() ? null : methodProceeds.get(0);
                    boolean foundBySearch = false;
                    for (MethodProceed proceed : methodProceeds) {
                        String search = normalizeConfigValue(proceed.getSearch());
                        if (search != null && sampleName != null && sampleName.contains(search)) {
                            proceeds.add(proceed);
                            foundBySearch = true;
                        }
                    }
                    if (!foundBySearch && defaultProceed != null) {
                        proceeds.add(defaultProceed);
                    }
                }
            }
        }
        return proceeds;
    }

    private String normalizeConfigValue(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() || "/".equals(trimmed) ? null : trimmed;
    }

    private double parseQuantity(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private int parseDilutionCount(String value) {
        String normalized = normalizeConfigValue(value);
        if (normalized == null) {
            return 1;
        }
        try {
            int count = Integer.parseInt(normalized.replaceAll("[^0-9]", ""));
            return count > 0 ? count : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private static class MediaUsage {
        private final String medium;
        private int count;
        private double totalQuantity;
        private String unitQuantityText;
        private String nonNumericQuantityText;

        private MediaUsage(String medium) {
            this.medium = medium;
        }

        private Map<String, Object> toMap() {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("medium", medium);
            item.put("count", count);
            item.put("display", display());
            return item;
        }

        private String display() {
            if (totalQuantity > 0) {
                String totalText = totalQuantity == Math.rint(totalQuantity)
                        ? String.valueOf((long) totalQuantity)
                        : String.format("%.2f", totalQuantity);
                return totalText + " ml";
            }
            return nonNumericQuantityText == null ? count + " 次" : nonNumericQuantityText + " x " + count;
        }
    }
}
