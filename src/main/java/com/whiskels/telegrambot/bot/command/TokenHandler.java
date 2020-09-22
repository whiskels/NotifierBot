package com.whiskels.telegrambot.bot.command;

import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.TOKEN;
import static com.whiskels.telegrambot.util.TelegramUtils.createMessageTemplate;

@Component
@Slf4j
public class TokenHandler extends AbstractBaseHandler {
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(User user, String message) {
        return Collections.singletonList(createMessageTemplate(user)
                .setText(String.format("Your token is *%s*", user.getChatId())));
    }

    @Override
    public Command supportedCommand() {
        return TOKEN;
    }
}
