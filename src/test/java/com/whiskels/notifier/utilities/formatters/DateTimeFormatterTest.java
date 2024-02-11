package com.whiskels.notifier.utilities.formatters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeFormatterTest {

    @Test
    @DisplayName("Should correctly represent birthday format")
    void birthdayFormatPattern() {
        assertEquals("dd.MM", DateTimeFormatter.BIRTHDAY_FORMAT);
    }

    @Test
    @DisplayName("Should correctly convert date")
    void dayMonthYearFormatter() {
        LocalDate date = LocalDate.of(2023, 8, 21);
        String formatted = date.format(DateTimeFormatter.DAY_MONTH_YEAR_FORMATTER);

        assertEquals("21-08-2023", formatted);
    }

    @Test
    @DisplayName("Should correctly format birthday")
    void birthdayFormatter() {
        LocalDate date = LocalDate.of(2023, 8, 21);
        String formatted = date.format(DateTimeFormatter.BIRTHDAY_FORMATTER);

        assertEquals("21.08", formatted);
    }
}