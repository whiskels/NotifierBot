package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.ScheduledCommandHandler;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.security.Secured;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.whiskels.notifier.telegram.Command.SCHEDULE_HELP;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;

@Service
@ConditionalOnBean(ScheduledCommandHandler.class)
class ScheduleHelpHandler implements CommandHandler {
    @Override
    @Secured({MANAGER, HEAD, ADMIN})
    public SendMessage handle(User user, String message) {
        return builder(user)
                .line("*Help message for /schedule command*")
                .line()
                .line("[/schedule *time*](/schedule time) - set daily message at time. Examples: ")
                .line("   /schedule 1 - 01:00")
                .line("   /schedule 10 - 10:00")
                .line("   /schedule 1230 - 12:30")
                .line("Please note that daily messages are not sent on *sundays and saturdays*!")
                .build();
    }

    @Override
    public Command getCommand() {
        return SCHEDULE_HELP;
    }
}
