package com.whiskels.notifier.common;

import com.whiskels.notifier.telegram.builder.ReportBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportBuilderTest {
    private static final String EXPECTED = String.format("*TEST_BUILDER on 22-03-2020:*%n%n" +
            "first line%n%n%n" +
            "third line%n" +
            "1, 2, 3%n" +
            "4%n" +
            "---------------------------%n" +
            "5%n" +
            "---------------------------%n" +
            "6%n" +
            "7%n" +
            "8%n" +
            "9%n" +
            "Nothing");

    @Test
    void testReportBuilding() {
        String actual = ReportBuilder.builder("TEST_BUILDER on" + reportDate(LocalDate.of(2020, 3, 22)))
                .line("first line")
                .line()
                .line("third line")
                .setActiveCollector(COLLECTOR_COMMA_SEPARATED)
                .list(List.of(1, 2, 3))
                .line()
                .setActiveCollector(COLLECTOR_EMPTY_LINE)
                .list(List.of(4, 5, 6))
                .line()
                .setActiveCollector(COLLECTOR_NEW_LINE)
                .list(List.of(7, 8, 9))
                .line()
                .setActiveCollector(COLLECTOR_COMMA_SEPARATED)
                .list(Collections.emptyList())
                .build();

        assertEquals(EXPECTED, actual);
    }
}
