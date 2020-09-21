package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.telegrambot.bot.command.Command.ADMIN_MESSAGE;

@Component
@Slf4j
public class AdminMessageHandler extends AbstractBaseHandler {
    private final UserService userService;

    public AdminMessageHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(User admin, Message message) {
        List<PartialBotApiMethod<? extends Serializable>> messagesToSend = userService.getUsers()
                .stream()
                .map(user -> createMessageTemplate(user)
                        .setText(message.getText()))
                .collect(Collectors.toList());

        messagesToSend.add(createMessageTemplate(admin)
                .setText(String.format("Notified %d users", messagesToSend.size())));

        return messagesToSend;
    }

    @Override
    public Command supportedCommand() {
        return ADMIN_MESSAGE;
    }
}
