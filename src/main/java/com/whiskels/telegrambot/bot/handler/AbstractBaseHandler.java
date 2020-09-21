package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public abstract class AbstractBaseHandler {
    protected static final String END_LINE = "\n";
    protected static final String EMPTY_LINE = "---------------------------";
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public abstract List<PartialBotApiMethod<? extends Serializable>> operate(User user, Message message);

    /*
     * Creates SendMessage template with markdown enabled for user with chatId
     */
    protected final SendMessage createMessageTemplate(User user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.enableMarkdown(true);

        return sendMessage;
    }

    public abstract Command supportedCommand();
}
