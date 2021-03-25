package com.whiskels.notifier.common.datetime;

import com.whiskels.notifier.common.datetime.impl.LocalDateTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static com.whiskels.notifier.common.datetime.DateTimeProviderTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalDateTimeProviderTest {
    private DateTimeProvider dateTimeProvider;

    @BeforeEach
    void init() {
        dateTimeProvider = new LocalDateTimeProvider();
    }

    @Test
    void testSubtractWorkingDays() {
        assertEquals(FRIDAY, dateTimeProvider.subtractWorkingDays(MONDAY, 1));
        assertEquals(FRIDAY, dateTimeProvider.subtractWorkingDays(SATURDAY, 1));
        assertEquals(FRIDAY, dateTimeProvider.subtractWorkingDays(SUNDAY, 1));
        assertEquals(MONDAY, dateTimeProvider.subtractWorkingDays(MONDAY, 0));
        assertEquals(MONDAY.minusDays(7), dateTimeProvider.subtractWorkingDays(MONDAY, 5));
    }

    @Test
    void testToLocalDate() {
        assertEquals(FRIDAY, dateTimeProvider.toLocalDate(new Date(121, Calendar.MARCH, 19)));

    }

    @Test
    void testNow() {
        assertEquals(LocalDateTime.now(), dateTimeProvider.now());
        assertEquals(LocalDate.now(), dateTimeProvider.today());
    }
}
