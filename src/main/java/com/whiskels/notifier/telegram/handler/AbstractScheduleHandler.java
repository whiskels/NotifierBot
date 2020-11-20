package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.service.ScheduleService;
import lombok.RequiredArgsConstructor;

/**
 * Base class for handlers that affect user's schedule
 */
@RequiredArgsConstructor
public abstract class AbstractScheduleHandler extends AbstractBaseHandler {
    protected final ScheduleService scheduleService;
}
