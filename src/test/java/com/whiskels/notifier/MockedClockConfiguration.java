package com.whiskels.notifier;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@TestConfiguration

public class MockedClockConfiguration {
    private static final String EXPECTED_BOT_TIME = "2014-12-22T10:15:30Z";
    public static final LocalDate EXPECTED_DATE = LocalDate.of(2014, 12, 22);

    @Bean
    Clock mockedClock() {
        return Clock.fixed(Instant.parse(EXPECTED_BOT_TIME), ZoneId.of("UTC"));
    }
}
