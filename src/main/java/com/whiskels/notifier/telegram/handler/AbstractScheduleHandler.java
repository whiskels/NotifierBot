package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for handlers that affect user's schedule
 */
public abstract class AbstractScheduleHandler extends AbstractBaseHandler {
    @Autowired
    protected ScheduleService scheduleService;
}
