package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.SCHEDULE_HELP;

@Component
@Slf4j
public class ScheduleHelpHandler extends AbstractBaseHandler {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(User user, Message message) {
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
                .append("[/schedule get](/schedule get) - show current schedule state")
                .append(END_LINE)
                .append("[/schedule clear](/schedule clear) - clear schedule")
                .append(END_LINE)
                .append("[/schedule help](/schedule help) - show help message")
                .append(END_LINE);
        sendMessage.setText(text.toString());

        return Collections.singletonList(sendMessage);
    }

    @Override
    public Command supportedCommand() {
        return SCHEDULE_HELP;
    }
}
