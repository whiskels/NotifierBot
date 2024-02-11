package com.whiskels.notifier.infrastructure.config.clock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@Profile("!test")
class ClockConfiguration {
    @Bean
    Clock defaultClock(@Value("${common.timezone}") String timeZone) {
        return Clock.system(ZoneId.of(timeZone));
    }
}
