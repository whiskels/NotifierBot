package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.BotCommand;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.RequiredRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.model.Role.*;
import static com.whiskels.telegrambot.util.TelegramUtil.END_LINE;
import static com.whiskels.telegrambot.util.TelegramUtil.createMessageTemplate;

/**
 * Shows help message for {@link ScheduleAddHandler} supported syntax
 *
 * Available to: Manager, Head, Admin
 */
@Component
@Slf4j
@BotCommand(command = "/SCHEDULE_HELP")
public class ScheduleHelpHandler extends AbstractBaseHandler {

    @Override
    @RequiredRoles(roles = {MANAGER, HEAD, ADMIN})
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage sendMessage = createMessageTemplate(user);

        StringBuilder text = new StringBuilder();
        text.append("*Help message for /schedule command*")
                .append(END_LINE)
                .append(END_LINE)
                .append("[/schedule *time*](/schedule time) - set daily message at time. Examples: ")
                .append(END_LINE)
                .append("   /schedule 1 - 01:00")
                .append(END_LINE)
                .append("   /schedule 10 - 10:00")
                .append(END_LINE)
                .append("   /schedule 1230 - 12:30")
                .append(END_LINE)
                .append("Please note that daily messages are not sent on *sundays and saturdays*!")
                .append(END_LINE)
                .append("[/schedule clear](/schedule_clear) - clear schedule")
                .append(END_LINE)
                .append("[/schedule help](/schedule_help) - show help message")
                .append(END_LINE);
        sendMessage.setText(text.toString());

        return Collections.singletonList(sendMessage);
    }
}
