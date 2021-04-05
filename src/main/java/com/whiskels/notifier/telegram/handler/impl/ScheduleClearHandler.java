package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractScheduleHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import com.whiskels.notifier.telegram.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;

import static com.whiskels.notifier.telegram.Command.SCHEDULE_CLEAR;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Clears schedule for user
 * <p>
 * Available to: Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = SCHEDULE_CLEAR, requiredRoles = {HR, MANAGER, HEAD, ADMIN})
@ConditionalOnBean(annotation = Schedulable.class)
public class ScheduleClearHandler extends AbstractScheduleHandler {
    public ScheduleClearHandler(AuthorizationService authorizationService,
                                ApplicationEventPublisher publisher,
                                ScheduleService scheduleService) {
        super(authorizationService, publisher, scheduleService);
    }

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /SCHEDULE_CLEAR");
        final int count = scheduleService.clear(user.getId());

        publish(create(user)
                .line("Your schedule (%d) was cleared", count)
                .build());
    }
}
