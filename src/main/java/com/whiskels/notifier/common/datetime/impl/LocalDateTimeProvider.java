package com.whiskels.notifier.common.datetime.impl;

import com.whiskels.notifier.common.datetime.DateTimeProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class LocalDateTimeProvider implements DateTimeProvider {
}
