package com.whiskels.telegrambot.bot.command;

import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.RequiredRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.model.Role.ADMIN;
import static com.whiskels.telegrambot.util.TelegramUtils.createMessageTemplate;

@Component
@Slf4j
@BotCommand(command = "/ADMIN_TIME")
public class AdminTimeHandler extends AbstractBaseHandler {
    @Override
    @RequiredRoles(roles = ADMIN)
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        return Collections.singletonList(createMessageTemplate(user)
                .setText(LocalDateTime.now().toString()));
    }
}
