package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.service.ScheduleService;

/**
 * Base class for handlers that affect user's schedule
 */
public abstract class AbstractScheduleHandler extends AbstractBaseHandler {
    protected final ScheduleService scheduleService;

    public AbstractScheduleHandler(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

}
