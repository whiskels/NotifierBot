package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.BotCommand;
import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.util.TelegramUtil.createMessageTemplate;

/**
 * Sends user his token
 *
 * Available to: everyone
 */
@Component
@Slf4j
@BotCommand(command = "/TOKEN")
public class TokenHandler extends AbstractBaseHandler {
    @Override
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /TOKEN");
        return Collections.singletonList(createMessageTemplate(user)
                .setText(String.format("Your token is *%s*", user.getChatId())));
    }
}
