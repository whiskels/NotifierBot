package com.whiskels.notifier.common.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static com.whiskels.notifier.common.util.DateTimeUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeUtilTest {
    public static final LocalDate FRIDAY = LocalDate.of(2021, 3, 19);
    public static final LocalDate SATURDAY = LocalDate.of(2021, 3, 20);
    public static final LocalDate SUNDAY = LocalDate.of(2021, 3, 21);
    public static final LocalDate MONDAY = LocalDate.of(2021, 3, 22);

    @Test
    void testSubtractWorkingDays() {
        assertEquals(FRIDAY, subtractWorkingDays(MONDAY, 1));
        assertEquals(FRIDAY, subtractWorkingDays(SATURDAY, 1));
        assertEquals(FRIDAY, subtractWorkingDays(SUNDAY, 1));
        assertEquals(MONDAY, subtractWorkingDays(MONDAY, 0));
        assertEquals(MONDAY.minusDays(7), subtractWorkingDays(MONDAY, 5));
    }

    @Test
    void testAddWorkingDays() {
        assertEquals(MONDAY, addWorkingDays(MONDAY, 0));
        assertEquals(MONDAY, addWorkingDays(SATURDAY, 1));
        assertEquals(MONDAY, addWorkingDays(SUNDAY, 1));
        assertEquals(MONDAY, addWorkingDays(FRIDAY, 1));
        assertEquals(MONDAY.plusDays(7), addWorkingDays(MONDAY, 5));
    }

    @Test
    void testToLocalDate() {
        assertEquals(FRIDAY, toLocalDate(new Date(121, Calendar.MARCH, 19)));
    }
}
