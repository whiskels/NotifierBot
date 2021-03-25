package com.whiskels.notifier.common.datetime.impl;

import com.whiskels.notifier.common.datetime.DateTimeProvider;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Profile("prod")
public class HerokuDateTimeProvider implements DateTimeProvider {
    @Value("${heroku.server.hour.offset:3}")
    @Setter
    private int serverHourOffset;

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now().plusHours(serverHourOffset);
    }
}
