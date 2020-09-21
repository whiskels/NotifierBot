package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.HELP;

@Component
@Slf4j
public class HelpHandler extends AbstractBaseHandler {
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(User user, Message message) {
        return Collections.singletonList(createMessageTemplate(user)
                .setText(String.format("*Help message for bot commands*%n%n" +
                        "[/start](/start) - show start message%n" +
                        "[/get](/get) - get your customer overdue debts info (updated daily)%n" +
                        "[/token](/token) - get your token and user info%n" +
                        "[/schedule help](/schedule help) - show schedule help message%n" +
                        "[/help](/help) - show help message%n")));
    }

    @Override
    public Command supportedCommand() {
        return HELP;
    }
}
