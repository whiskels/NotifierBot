package com.whiskels.notifier.common.datetime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@Profile("!test")
public class ClockConfiguration {
    @Value("${common.timezone:Europe/Moscow}")
    private String timeZone;

    @Bean
    public Clock defaultClock() {
        return Clock.system(ZoneId.of(timeZone));
    }
}
