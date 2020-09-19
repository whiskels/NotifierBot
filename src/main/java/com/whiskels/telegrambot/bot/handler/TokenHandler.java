package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.TOKEN;

@Component
@Slf4j
public class TokenHandler extends AbstractHandler {
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(String chatId, Message message) {
        return Collections.singletonList(createMessageTemplate(chatId)
                .setText(String.format("Your token is *%s*", chatId)));
    }

    @Override
    public Command supportedCommand() {
        return TOKEN;
    }
}
