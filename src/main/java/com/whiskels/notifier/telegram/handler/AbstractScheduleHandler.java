package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.security.AuthorizationService;
import com.whiskels.notifier.telegram.service.ScheduleService;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Base class for handlers that affect user's schedule
 */
public abstract class AbstractScheduleHandler extends AbstractBaseHandler {
    protected final ScheduleService scheduleService;

    public AbstractScheduleHandler(AuthorizationService authorizationService,
                                   ApplicationEventPublisher publisher,
                                   ScheduleService scheduleService) {
        super(authorizationService, publisher);
        this.scheduleService = scheduleService;
    }
}
