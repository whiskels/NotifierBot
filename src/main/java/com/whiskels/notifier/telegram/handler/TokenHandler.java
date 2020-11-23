package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.User;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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
@Profile({"telegram", "telegram-test"})
public class TokenHandler extends AbstractBaseHandler {
    @Override
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /TOKEN");
        return List.of(MessageBuilder.create(user)
                .line("Your token is *%s*", user.getChatId())
                .build());
    }
}
