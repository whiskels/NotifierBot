package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractScheduleHandler;
import com.whiskels.notifier.telegram.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Clears schedule for user
 * <p>
 * Available to: Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = "/SCHEDULE_CLEAR", requiredRoles = {HR, MANAGER, HEAD, ADMIN})
@ConditionalOnBean(annotation = Schedulable.class)
public class ScheduleClearHandler extends AbstractScheduleHandler {
    public ScheduleClearHandler(ScheduleService scheduleService) {
        super(scheduleService);
    }

    @Override
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /SCHEDULE_CLEAR");
        final int count = scheduleService.clear(user.getId());

        return List.of(MessageBuilder.create(user)
                .line("Your schedule (%d) was cleared", count)
                .build());
    }
}
