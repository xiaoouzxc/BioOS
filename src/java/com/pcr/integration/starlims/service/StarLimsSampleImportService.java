package com.pcr.integration.starlims.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pcr.integration.starlims.dto.StarLimsSampleImportResult;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StarLimsSampleImportService {

    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter IMPORT_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId IMPORT_ZONE = ZoneId.of("Asia/Shanghai");

    private static final List<String> LIMS_COLUMNS = List.of(
            "优先级",
            "制样编号",
            "申请单号",
            "样品编号",
            "样品短号",
            "样品名称",
            "报告抬头",
            "业务类型",
            "周期类型",
            "录单客服",
            "跟单客服",
            "样品客服接收日期",
            "制样或发样日期",
            "发样接收人",
            "预计出报告时间",
            "预计出数据时间",
            "检测组别",
            "检测人",
            "测试代码",
            "检测项目",
            "条款代码",
            "检测方法",
            "分析项",
            "低限",
            "高限",
            "文本限值",
            "报告单位",
            "备注",
            "检测样数量",
            "留样数量",
            "中心实验室",
            "限值备注",
            "REP",
            "业务"
    );

    private final JdbcTemplate jdbcTemplate;
    private final AtomicLong importVersion = new AtomicLong(0);
    private volatile String lastImportTime = "";

    public StarLimsSampleImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public StarLimsSampleImportResult importDataset(JsonNode dataset) {
        List<Map<String, Object>> rows = extractRows(dataset);
        return importRows(rows, "lims-json");
    }

    public StarLimsSampleImportResult importExcel(File excelFile) throws IOException {
        List<Map<String, Object>> rows = readExcelRows(excelFile);
        return importRows(rows, "excel");
    }

    private StarLimsSampleImportResult importRows(List<Map<String, Object>> rows, String source) {
        LocalDateTime importTime = LocalDateTime.now(IMPORT_ZONE);
        String importTimeText = IMPORT_TIME_FORMATTER.format(importTime);
        String tableName = "total_samples_" + YEAR_FORMATTER.format(importTime);

        ensureYearTable(tableName);

        StarLimsSampleImportResult result = new StarLimsSampleImportResult();
        result.setTableName(tableName);
        result.setImportTime(importTimeText);
        result.setSource(source);

        if (rows.isEmpty()) {
            result.setSuccess(true);
            result.setMessage("没有可导入的样品数据");
            result.setImportedCount(0);
            result.setSequence(nextSequence(tableName, importTime));
            return result;
        }

        int sequence = nextSequence(tableName, importTime);
        int nextDailySampleOrder = nextDailySampleOrder(tableName, importTimeText);
        Map<String, Integer> dailySampleOrderBySampleNumber = new LinkedHashMap<>();
        String insertSql = buildInsertSql(tableName);

        for (Map<String, Object> row : rows) {
            Object sampleNumberValue = toDatabaseValue(row.get("样品短号"));
            String sampleNumber = sampleNumberValue == null ? "" : String.valueOf(sampleNumberValue);
            Integer dailySampleOrder = dailySampleOrderBySampleNumber.get(sampleNumber);
            if (dailySampleOrder == null) {
                dailySampleOrder = nextDailySampleOrder++;
                dailySampleOrderBySampleNumber.put(sampleNumber, dailySampleOrder);
            }
            List<Object> values = new ArrayList<>();
            for (String column : LIMS_COLUMNS) {
                values.add(toDatabaseValue(row.get(column)));
            }
            values.add(0);
            values.add(null);
            values.add(null);
            values.add(null);
            values.add(sequence);
            values.add(importTimeText);
            values.add(dailySampleOrder);
            jdbcTemplate.update(insertSql, values.toArray());
        }

        result.setSuccess(true);
        result.setMessage("样品数据导入成功，共 " + rows.size() + " 条");
        result.setImportedCount(rows.size());
        result.setSequence(sequence);
        lastImportTime = importTimeText;
        importVersion.incrementAndGet();
        return result;
    }

    public long getImportVersion() {
        return importVersion.get();
    }

    public String getLastImportTime() {
        return lastImportTime;
    }

    private List<Map<String, Object>> extractRows(JsonNode dataset) {
        List<Map<String, Object>> rows = new ArrayList<>();
        JsonNode dataRows = dataset == null ? null : dataset.path("Tables").path(0).path("Rows");

        if (dataRows == null || !dataRows.isArray()) {
            return rows;
        }

        for (JsonNode dataRow : dataRows) {
            Map<String, Object> row = new LinkedHashMap<>();

            for (String column : LIMS_COLUMNS) {
                JsonNode value = dataRow.get(column);

                if (value == null || value.isNull()) {
                    row.put(column, "noneMessage");
                } else if (value.isNumber()) {
                    row.put(column, value.numberValue());
                } else if (value.isBoolean()) {
                    row.put(column, value.booleanValue());
                } else {
                    String text = value.asText();
                    row.put(column, isBlank(text) ? "noneMessage" : text);
                }
            }

            rows.add(row);
        }

        return rows;
    }

    private List<Map<String, Object>> readExcelRows(File excelFile) throws IOException {
        List<Map<String, Object>> rows = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream inputStream = new FileInputStream(excelFile);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFRow headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return rows;
            }

            Map<Integer, String> headerByIndex = new LinkedHashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = formatter.formatCellValue(headerRow.getCell(i));
                if (LIMS_COLUMNS.contains(header)) {
                    headerByIndex.put(i, header);
                }
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow sheetRow = sheet.getRow(rowIndex);
                if (sheetRow == null) {
                    continue;
                }

                Map<String, Object> row = new LinkedHashMap<>();
                boolean hasValue = false;
                for (Map.Entry<Integer, String> entry : headerByIndex.entrySet()) {
                    String value = formatter.formatCellValue(sheetRow.getCell(entry.getKey()));
                    if (value != null && !value.trim().isEmpty()) {
                        hasValue = true;
                    }
                    row.put(entry.getValue(), isBlank(value) ? null : value);
                }

                if (hasValue) {
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    private void ensureYearTable(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(identifier(tableName)).append(" (");
        sql.append("`id` int NOT NULL AUTO_INCREMENT,");
        for (String column : LIMS_COLUMNS) {
            sql.append(identifier(column)).append(" TEXT NULL,");
        }
        sql.append("`done` int NOT NULL DEFAULT 0,");
        sql.append("`复测` varchar(20) NULL,");
        sql.append("`结果` varchar(100) NULL,");
        sql.append("`位置` varchar(20) NULL,");
        sql.append("`顺序` int NOT NULL DEFAULT 0,");
        sql.append("`传入时间` datetime NOT NULL,");
        sql.append("`当天样品序号` int NULL,");
        sql.append("`做样顺序` int NULL,");
        sql.append("`做样时间` datetime NULL,");
        sql.append("PRIMARY KEY (`id`)");
        sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
        jdbcTemplate.execute(sql.toString());
        ensureDailySampleOrderColumn(tableName);
        ensureColumn(tableName, "做样顺序", "`做样顺序` int NULL AFTER `当天样品序号`");
        ensureColumn(tableName, "做样时间", "`做样时间` datetime NULL AFTER `做样顺序`");
    }

    private void ensureDailySampleOrderColumn(String tableName) {
        ensureColumn(tableName, "当天样品序号", "`当天样品序号` int NULL AFTER `传入时间`");
    }

    private void ensureColumn(String tableName, String columnName, String columnDefinition) {
        String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName, columnName);
        if (count == null || count == 0) {
            jdbcTemplate.execute("ALTER TABLE " + identifier(tableName)
                    + " ADD COLUMN " + columnDefinition);
        }
    }

    private int nextSequence(String tableName, LocalDateTime importTime) {
        LocalDate importDate = importTime.toLocalDate();
        String startOfDay = IMPORT_TIME_FORMATTER.format(importDate.atStartOfDay());
        String startOfNextDay = IMPORT_TIME_FORMATTER.format(importDate.plusDays(1).atStartOfDay());
        Integer max = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(`顺序`), 0) FROM " + identifier(tableName)
                        + " WHERE `传入时间` >= ? AND `传入时间` < ?",
                Integer.class,
                startOfDay,
                startOfNextDay
        );
        return (max == null ? 0 : max) + 1;
    }

    private int nextDailySampleOrder(String tableName, String importTimeText) {
        Integer max = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(`当天样品序号`), 0) FROM " + identifier(tableName)
                        + " WHERE DATE(`传入时间`) = DATE(?)",
                Integer.class,
                importTimeText
        );
        return (max == null ? 0 : max) + 1;
    }

    private String buildInsertSql(String tableName) {
        List<String> columns = new ArrayList<>(LIMS_COLUMNS);
        columns.add("done");
        columns.add("复测");
        columns.add("结果");
        columns.add("位置");
        columns.add("顺序");
        columns.add("传入时间");
        columns.add("当天样品序号");

        StringJoiner columnSql = new StringJoiner(",");
        StringJoiner placeholders = new StringJoiner(",");
        for (String column : columns) {
            columnSql.add(identifier(column));
            placeholders.add("?");
        }

        return "INSERT INTO " + identifier(tableName)
                + " (" + columnSql + ") VALUES (" + placeholders + ")";
    }

    private static Object toDatabaseValue(Object value) {
        if (value == null) {
            return "noneMessage";
        }

        String text = String.valueOf(value).trim();

        return isBlank(text) ? "noneMessage" : text;
    }

    private static String identifier(String value) {
        return "`" + value.replace("`", "``") + "`";
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
