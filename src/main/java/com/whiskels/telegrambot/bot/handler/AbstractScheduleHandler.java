package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.service.ScheduleService;
import lombok.RequiredArgsConstructor;

/**
 * Base class for handlers that affect user's schedule
 */
@RequiredArgsConstructor
public abstract class AbstractScheduleHandler extends AbstractBaseHandler {
    protected final ScheduleService scheduleService;
}
