package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.annotations.BotCommand;
import com.whiskels.telegrambot.bot.builder.MessageBuilder;
import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

/**
 * Sends user his token
 * <p>
 * Available to: everyone
 */
@Component
@Slf4j
@BotCommand(command = "/TOKEN", message = "Show your token")
public class TokenHandler extends AbstractBaseHandler {
    @Override
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /TOKEN");
        return List.of(MessageBuilder.create(user)
                .line("Your token is *%s*", user.getChatId())
                .build());
    }
}
