package com.whiskels.notifier;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.*;

@TestConfiguration
public class MockedClockConfiguration {
    private static final String EXPECTED_BOT_TIME = "2014-01-22T10:15:30Z";
    public static final LocalDate EXPECTED_DATE = LocalDate.of(2014, 1, 22);
    public static final LocalTime EXPECTED_TIME = LocalTime.of(10, 15, 30);

    @Bean
    protected Clock mockedClock() {
        return Clock.fixed(Instant.parse(EXPECTED_BOT_TIME), ZoneId.of("UTC"));
    }
}
