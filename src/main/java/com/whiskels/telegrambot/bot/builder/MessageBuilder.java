package com.whiskels.telegrambot.bot.builder;


import com.whiskels.telegrambot.model.User;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public final class MessageBuilder {
    @Setter
    private String chatId;
    private final StringBuilder sb = new StringBuilder();
    private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    private List<InlineKeyboardButton> row = null;

    private MessageBuilder() {
    }

    public static MessageBuilder create(String chatId) {
        MessageBuilder builder = new MessageBuilder();
        builder.setChatId(chatId);
        return builder;
    }

    public static MessageBuilder create(User user) {
        return create(String.valueOf(user.getChatId()));
    }

    public MessageBuilder text(String text, Object... args) {
        sb.append(String.format(text, args));
        return this;
    }

    public MessageBuilder row() {
        addRowToKeyboard();
        row = new ArrayList<>();
        return this;
    }

    public MessageBuilder button(String text, String callbackData) {
        row.add(new InlineKeyboardButton().setText(text).setCallbackData(callbackData));
        return this;
    }

    public MessageBuilder buttonWithArguments(String text, String callbackData) {
        return button(text, callbackData + " " + text);
    }

    public SendMessage build() {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .enableMarkdown(true)
                .setText(sb.toString());

        addRowToKeyboard();

        if (!keyboard.isEmpty()) {
            sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(keyboard));
        }

        return sendMessage;
    }

    private void addRowToKeyboard() {
        if (row != null) {
            keyboard.add(row);
        }
    }
}
