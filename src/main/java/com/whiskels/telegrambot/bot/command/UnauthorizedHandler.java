package com.whiskels.telegrambot.bot.command;

import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.telegrambot.util.TelegramUtils.createMessageTemplate;


@Component
@Slf4j
public class UnauthorizedHandler extends AbstractBaseHandler {
    private final UserService userService;

    public UnauthorizedHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        List<PartialBotApiMethod<? extends Serializable>> messagesToSend = new ArrayList<>();
        messagesToSend.add(createMessageTemplate(user).setText(
                String.format("Your token is *%s*%nPlease contact your supervisor to gain access", user.getChatId())));
        messagesToSend.addAll(prepareStatusMessageToAdmins(user, message));
        return messagesToSend;
    }

    /*
     * Sends status message about unauthorized user to admins
     */
    protected List<SendMessage> prepareStatusMessageToAdmins(User user, String message) {
        return userService.getAdmins().stream()
                .map(admin -> createMessageTemplate(admin)
                        .setText(String.format("Unauthorized user *%s*%nMessage: %s",
                                user.getChatId(), message)))
                .collect(Collectors.toList());
    }
}
