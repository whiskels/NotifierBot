package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sends user his token
 * <p>
 * Available to: everyone
 */
@Slf4j
@BotCommand(command = "/TOKEN", message = "Show your token")
public class TokenHandler extends AbstractBaseHandler {
    @Override
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /TOKEN");
        return List.of(MessageBuilder.create(user)
                .line("Your token is *%s*", user.getChatId())
                .line("Your roles are: {}", user.getRoles().stream().map(Enum::toString).collect(Collectors.joining(", ")))
                .build());
    }
}
