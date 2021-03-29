package com.whiskels.notifier.common;

import com.whiskels.notifier.telegram.builder.ReportBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.FormatUtil.*;
import static com.whiskels.notifier.common.StreamUtil.alwaysTruePredicate;
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
        String actual = ReportBuilder.withHeader("TEST_BUILDER", LocalDate.of(2020, 3, 22))
                .line("first line")
                .line()
                .line("third line")
                .list(List.of(1, 2, 3), alwaysTruePredicate(), COLLECTOR_COMMA_SEPARATED)
                .line()
                .list(List.of(4, 5, 6), alwaysTruePredicate(), COLLECTOR_EMPTY_LINE)
                .line()
                .list(List.of(7, 8, 9), alwaysTruePredicate(), COLLECTOR_NEW_LINE)
                .line()
                .list(List.of(0), x -> false, COLLECTOR_COMMA_SEPARATED)
                .build();

        assertEquals(EXPECTED, actual);
    }
}
