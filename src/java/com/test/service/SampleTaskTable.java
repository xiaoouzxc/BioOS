package com.test.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class SampleTaskTable {
    private static final ZoneId TABLE_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");

    private SampleTaskTable() {
    }

    public static String currentYearTableName() {
        return "total_samples_" + YEAR_FORMATTER.format(LocalDate.now(TABLE_ZONE));
    }
}
