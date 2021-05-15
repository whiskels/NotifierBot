package com.whiskels.notifier.telegram.builder;


import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.domain.User;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * MessageBuilder is used to build instances of {@link org.telegram.telegrambots.meta.api.methods.send.SendMessage}
 * <p>
 * MessageBuilder provides useful methods that simplify creation of bot replies
 */
public final class MessageBuilder {
    @Setter
    private String chatId;
    private final StringBuilder sb = new StringBuilder();
    private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    private List<InlineKeyboardButton> row = null;

    private MessageBuilder() {
    }

    /**
     * Creates new MessageBuilder with initialized chatId
     *
     * @param chatId of user that will receive the message
     * @return instance of MessageBuilder with initialized chatId
     */
    public static MessageBuilder builder(String chatId) {
        MessageBuilder builder = new MessageBuilder();
        builder.setChatId(chatId);
        return builder;
    }

    /**
     * Creates new MessageBuilder with initialized chatId
     *
     * @param user that will receive the message
     * @return instance of MessageBuilder with initialized chatId
     */
    public static MessageBuilder builder(User user) {
        return builder(String.valueOf(user.getChatId()));
    }

    /**
     * Simplified use of {@link String#format(String, Object...) String.format} that adds new formatted line to the
     * inner instance of {@link StringBuilder}
     *
     * @param text first agrument of {@link String#format(String, Object...) String.format}
     * @param args second and following arguments of {@link String#format(String, Object...) String.format}
     * @return this
     */
    public MessageBuilder line(String text, Object... args) {
        sb.append(String.format(text, args));
        return line();
    }

    /**
     * Creates new line break
     *
     * @return this
     */
    public MessageBuilder line() {
        sb.append(String.format("%n"));
        return this;
    }

    /**
     * Creates new {@link InlineKeyboardButton} row
     *
     * @return this
     */
    public MessageBuilder row() {
        addRowToKeyboard();
        row = new ArrayList<>();
        return this;
    }

    /**
     * Creates new {@link InlineKeyboardButton}
     *
     * @param text         button text
     * @param callbackData on click callback
     * @return this
     */
    public MessageBuilder button(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        row.add(button);
        return this;
    }

    /**
     * Creates new {@link InlineKeyboardButton}
     *
     * @param text         button text
     * @param command on click callback
     * @return this
     */
    public MessageBuilder button(String text, Command command) {
        return button(text, command.toString());
    }

    /**
     * Creates new {@link InlineKeyboardButton}
     *
     * @param text         button text (and callback argument)
     * @param command on click callback
     * @return this
     */
    public MessageBuilder buttonWithArguments(String text, Command command) {
        return button(text, command.toString() + " " + text);
    }

    /**
     * Builds an instance of {@link SendMessage}
     *
     * @return {@link SendMessage}
     */
    public SendMessage build() {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        sendMessage.setText(sb.toString().replace("_", " "));

        addRowToKeyboard();

        if (!keyboard.isEmpty()) {
            var markup = new InlineKeyboardMarkup();
            markup.setKeyboard(keyboard);
            sendMessage.setReplyMarkup(markup);
        }

        return sendMessage;
    }

    /**
     * Adds new row to keyboard.
     * Performs null check of current row
     */
    private void addRowToKeyboard() {
        if (row != null) {
            keyboard.add(row);
        }
    }
}
