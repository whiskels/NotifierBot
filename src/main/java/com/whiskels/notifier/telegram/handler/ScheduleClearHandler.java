package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.ScheduledCommandHandler;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import com.whiskels.notifier.telegram.security.Secured;
import com.whiskels.notifier.telegram.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import static com.whiskels.notifier.telegram.Command.SCHEDULE_CLEAR;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;

@Service
@ConditionalOnBean(ScheduledCommandHandler.class)
@RequiredArgsConstructor
class ScheduleClearHandler implements CommandHandler {
    private final ScheduleService scheduleService;
    private final SendMessageCreationEventPublisher publisher;

    @Override
    @Secured({MANAGER, HEAD, ADMIN})
    public void handle(User user, String message) {
        final int count = scheduleService.clear(user.getId());

        publisher.publish(builder(user)
                .line("Your schedule (%d) was cleared", count)
                .build());
    }

    @Override
    public Command getCommand() {
        return SCHEDULE_CLEAR;
    }
}
