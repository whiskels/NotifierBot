package com.whiskels.notifier.utilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

import static com.whiskels.notifier.utilities.DateTimeUtil.parseDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateTimeUtilTest {

    @Test
    @DisplayName("Should subtract working days")
    void subtractWorkingDays() {
        LocalDate startDate = LocalDate.of(2023, 8, 21); // a Monday
        LocalDate resultDate = DateTimeUtil.subtractWorkingDays(startDate, 5);

        assertEquals(LocalDate.of(2023, 8, 14), resultDate);
    }

    @Test
    @DisplayName("Should handle zero when subtracting working days")
    void subtractWorkingDaysZero() {
        LocalDate startDate = LocalDate.of(2023, 8, 21); // a Monday
        LocalDate resultDate = DateTimeUtil.subtractWorkingDays(startDate, 0);

        assertEquals(resultDate, resultDate);
    }

    @Test
    @DisplayName("Should handle negatives when subtracting working days")
    void subtractWorkingDaysNegative() {
        LocalDate startDate = LocalDate.of(2023, 8, 21); // a Monday
        LocalDate resultDate = DateTimeUtil.subtractWorkingDays(startDate, -1);

        assertEquals(startDate, resultDate);
    }

    @Test
    @DisplayName("Should parse date from text")
    void parseDateTest() {
        assertEquals(LocalDate.of(2020,1,1), parseDate("01.01"));
        assertNull(parseDate(""));
        assertNull(parseDate(null));
        assertNull(parseDate("ILLEGAL"));
    }

    @Test
    @DisplayName("Should convert to report date")
    void reportDate() {
        LocalDate date = LocalDate.of(2023, 8, 21);
        String result = DateTimeUtil.reportDate(date);

        assertEquals(" 21-08-2023", result);
    }

    @Test
    @DisplayName("Should return true if date is same month")
    void isSameMonth_true() {
        assertTrue(DateTimeUtil.isSameMonth(LocalDate.of(2023, 8, 21), LocalDate.of(2023, 8, 5)));
    }

    @Test
    @DisplayName("Should return false if date is not same month")
    void isSameMonth_false() {
        assertFalse(DateTimeUtil.isSameMonth(LocalDate.of(2023, 8, 21), LocalDate.of(2023, 9, 1)));
        assertFalse(DateTimeUtil.isSameMonth((LocalDate) null, LocalDate.of(2023, 9, 1)));
    }

    @Test
    @DisplayName("Should return true if date is same day")
    void isSameDay_true() {
        assertTrue(DateTimeUtil.isSameDay(LocalDate.of(2023, 5, 21), LocalDate.of(2023, 5, 21)));
    }

    @Test
    @DisplayName("Should return false if date is not same day")
    void isSameDay_false() {
        assertFalse(DateTimeUtil.isSameDay(LocalDate.of(2023, 5, 21), LocalDate.of(2023, 5, 22)));
        assertFalse(DateTimeUtil.isSameDay((LocalDate) null, LocalDate.of(2023, 5, 22)));
    }

    @Test
    @DisplayName("Should return true if date is later")
    void isLater_true() {
        assertTrue(DateTimeUtil.isLater(LocalDate.of(2023, 8, 21), LocalDate.of(2023, 8, 20)));
    }

    @Test
    @DisplayName("Should return false if date is not later")
    void isLater_false() {
        assertFalse(DateTimeUtil.isLater(LocalDate.of(2023, 8, 21), LocalDate.of(2023, 8, 22)));
    }

    @Test
    @DisplayName("Should return true via predicate if date is same day")
    void isSameDay_predicate_true() {
        Function<String, LocalDate> func = s -> LocalDate.of(2023, 8, 21);
        assertTrue(DateTimeUtil.isSameDay(func, LocalDate.of(2023, 8, 21)).test("test"));
    }

    @Test
    @DisplayName("Should return false via predicate if date is not same day")
    void isSameDay_predicate_false() {
        Function<String, LocalDate> func = s -> LocalDate.of(2023, 8, 21);
        assertFalse(DateTimeUtil.isSameDay(func, LocalDate.of(2023, 8, 22)).test("test"));
    }

    @Test
    @DisplayName("Should return true via predicate if date is same month")
    void isSameMonth_predicate_true() {
        Function<String, LocalDate> func = s -> LocalDate.of(2023, 8, 21);
        assertTrue(DateTimeUtil.isSameMonth(func, LocalDate.of(2023, 8, 21)).test("test"));
    }

    @Test
    @DisplayName("Should return false via predicate if date is not same month")
    void isSameMonth_predicate_false() {
        Function<String, LocalDate> func = s -> LocalDate.of(2023, 1, 21);
        assertFalse(DateTimeUtil.isSameMonth(func, LocalDate.of(2023, 8, 22)).test("test"));
    }
}