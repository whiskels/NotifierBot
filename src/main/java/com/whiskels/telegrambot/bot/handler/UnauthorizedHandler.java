package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.Bot;
import com.whiskels.telegrambot.bot.command.Command;
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
public class UnauthorizedHandler extends AbstractHandler {
    private final UserService userService;
    private final Bot bot;

    public UnauthorizedHandler(UserService userService, Bot bot) {
        this.userService = userService;
        this.bot = bot;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(String chatId, Message message) {
        List<PartialBotApiMethod<? extends Serializable>> messagesToSend = new ArrayList<>();
        messagesToSend.add(createMessageTemplate(chatId).setText(
                String.format("Your token is *%s*%nPlease contact your supervisor to gain access", chatId)));
        messagesToSend.addAll(prepareStatusMessageToAdmins(chatId));
        return messagesToSend;
    }

    /*
     * Sends status message about unauthorized user to admins
     */
    protected List<SendMessage> prepareStatusMessageToAdmins(String chatId) {
        return userService.getAdmins().stream()
                .map(a -> createMessageTemplate(a.getChatId())
                        .setText(String.format("Unauthorized user *%s*", chatId)))
                .collect(Collectors.toList());
    }

    @Override
    public Command supportedCommand() {
        return UNAUTHORIZED;
    }
}
