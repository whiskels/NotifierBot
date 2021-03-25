package com.whiskels.notifier.common.datetime;

import com.whiskels.notifier.common.datetime.impl.HerokuDateTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HerokuLocalDateTimeProviderTest {
    private HerokuDateTimeProvider dateTimeProvider;

    private final int offset = 24;

    @BeforeEach
    void init() {
        dateTimeProvider = new HerokuDateTimeProvider();
        dateTimeProvider.setServerHourOffset(offset);
    }

    @Test
    void testNow() {
        assertEquals(LocalDateTime.now().plusHours(offset), dateTimeProvider.now());
        assertEquals(LocalDateTime.now().plusHours(offset).toLocalDate(), dateTimeProvider.today());
    }
}
