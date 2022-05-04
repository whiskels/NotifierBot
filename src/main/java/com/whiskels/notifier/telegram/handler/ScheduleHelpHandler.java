package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.ScheduledCommandHandler;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import com.whiskels.notifier.telegram.security.Secured;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import static com.whiskels.notifier.telegram.Command.SCHEDULE_HELP;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;

@Service
@ConditionalOnBean(ScheduledCommandHandler.class)
@RequiredArgsConstructor
class ScheduleHelpHandler implements CommandHandler {
    private final SendMessageCreationEventPublisher publisher;

    @Override
    @Secured({MANAGER, HEAD, ADMIN})
    public void handle(User user, String message) {
        publisher.publish(builder(user)
                .line("*Help message for /schedule command*")
                .line()
                .line("[/schedule *time*](/schedule time) - set daily message at time. Examples: ")
                .line("   /schedule 1 - 01:00")
                .line("   /schedule 10 - 10:00")
                .line("   /schedule 1230 - 12:30")
                .line("Please note that daily messages are not sent on *sundays and saturdays*!")
                .build());
    }

    @Override
    public Command getCommand() {
        return SCHEDULE_HELP;
    }
}
