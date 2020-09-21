package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.START;

@Component
@PropertySource("classpath:bot/bot.properties")
@Slf4j
public class StartHandler extends AbstractBaseHandler {
    @Value("${bot.name.test}")
    private String botUsername;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(User user, Message message) {
        return Collections.singletonList(createMessageTemplate(user)
                .setText(String.format("Hello. I'm  *%s*%nI can show overdue debts%n with [/get] command",
                        botUsername)));
    }

    @Override
    public Command supportedCommand() {
        return START;
    }
}
