package com.whiskels.telegrambot.util;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.*;

public class TelegramUtils {
    public static final InlineKeyboardButton TOKEN_BUTTON = createInlineKeyboardButton("Show your token", TOKEN);
    public static final InlineKeyboardButton GET_BUTTON = createInlineKeyboardButton("Get customer overdue debts", GET);
    public static final InlineKeyboardButton SCHEDULE_ADD_BUTTON = createInlineKeyboardButton("Schedule message", SCHEDULE);
    public static final InlineKeyboardButton SCHEDULE_GET_BUTTON = createInlineKeyboardButton("Show schedule", SCHEDULE_GET);
    public static final InlineKeyboardButton SCHEDULE_HELP_BUTTON = createInlineKeyboardButton("Schedule help", SCHEDULE_HELP);
    public static final InlineKeyboardButton SCHEDULE_CLEAR_BUTTON = createInlineKeyboardButton("Clear schedule", SCHEDULE_CLEAR);

    public static final List<InlineKeyboardButton> UNAUTHORIZED_ROW = List.of(TOKEN_BUTTON);
    public static final List<InlineKeyboardButton> GET_ROW = List.of(GET_BUTTON);
    public static final List<InlineKeyboardButton> SCHEDULE_ADD_ROW = List.of(SCHEDULE_ADD_BUTTON, SCHEDULE_GET_BUTTON);
    public static final List<InlineKeyboardButton> SCHEDULE_MANAGE_ROW = List.of(SCHEDULE_CLEAR_BUTTON, SCHEDULE_HELP_BUTTON);


    /*
     * Creates SendMessage template with markdown enabled
     */
    public static SendMessage createMessageTemplate(User user) {
        return new SendMessage()
                .setChatId(String.valueOf(user.getChatId()))
                .enableMarkdown(true);
    }

    public static InlineKeyboardButton createInlineKeyboardButton(String text, Command command) {
        return createInlineKeyboardButton(text, command, false);
    }


    public static InlineKeyboardButton createInlineKeyboardButton(String text, Command command, boolean isCommandWithArgs) {
        final String callbackData = isCommandWithArgs ? String.format("/%s %s", command, text) : String.format("/%s", command);

        return new InlineKeyboardButton()
                .setText(text)
                .setCallbackData(callbackData);
    }
}
