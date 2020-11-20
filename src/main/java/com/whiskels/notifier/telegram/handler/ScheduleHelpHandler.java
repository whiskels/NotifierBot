package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.model.User;
import com.whiskels.notifier.telegram.annotations.RequiredRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.whiskels.notifier.model.Role.*;

/**
 * Shows help message for {@link ScheduleAddHandler} supported syntax
 * <p>
 * Available to: Manager, Head, Admin, HR
 */
@Component
@Slf4j
@BotCommand(command = "/SCHEDULE_HELP")
public class ScheduleHelpHandler extends AbstractBaseHandler {

    @Override
    @RequiredRoles(roles = {MANAGER, HEAD, ADMIN, HR})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /SCHEDULE_HELP");
        return List.of(MessageBuilder.create(user)
                .line("*Help message for /schedule command*")
                .line()
                .line("[/schedule *time*](/schedule time) - set daily message at time. Examples: ")
                .line("   /schedule 1 - 01:00")
                .line("   /schedule 10 - 10:00")
                .line("   /schedule 1230 - 12:30")
                .line("Please note that daily messages are not sent on *sundays and saturdays*!")
                .build());
    }
}
