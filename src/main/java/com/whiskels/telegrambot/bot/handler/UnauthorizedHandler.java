package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.Bot;
import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.telegrambot.bot.command.Command.UNAUTHORIZED;


@Component
@Slf4j
public class UnauthorizedHandler extends AbstractBaseHandler {
    private final UserService userService;

    public UnauthorizedHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(User user, Message message) {
        List<PartialBotApiMethod<? extends Serializable>> messagesToSend = new ArrayList<>();
        messagesToSend.add(createMessageTemplate(user).setText(
                String.format("Your token is *%s*%nPlease contact your supervisor to gain access", user.getChatId())));
        messagesToSend.addAll(prepareStatusMessageToAdmins(user.getChatId()));
        return messagesToSend;
    }

    /*
     * Sends status message about unauthorized user to admins
     */
    protected List<SendMessage> prepareStatusMessageToAdmins(int chatId) {
        return userService.getAdmins().stream()
                .map(admin -> createMessageTemplate(admin)
                        .setText(String.format("Unauthorized user *%s*", chatId)))
                .collect(Collectors.toList());
    }

    @Override
    public Command supportedCommand() {
        return UNAUTHORIZED;
    }
}
