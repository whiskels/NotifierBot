package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.annotation.Schedulable;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractScheduleHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import static com.whiskels.notifier.telegram.Command.SCHEDULE_CLEAR;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
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
    @Override
    protected void handle(User user, String message) {
        final int count = scheduleService.clear(user.getId());

        publish(builder(user)
                .line("Your schedule (%d) was cleared", count)
                .build());
    }
}
