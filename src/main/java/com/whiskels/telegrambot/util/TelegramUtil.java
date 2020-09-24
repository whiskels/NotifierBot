package com.whiskels.telegrambot.util;

import com.whiskels.telegrambot.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelegramUtil {
    public static final String END_LINE = "\n";
    public static final String EMPTY_LINE = "---------------------------";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static final InlineKeyboardButton TOKEN_BUTTON = createInlineKeyboardButton("Show your token", "/TOKEN");
    public static final InlineKeyboardButton GET_BUTTON = createInlineKeyboardButton("Get customer overdue debts", "/GET");
    public static final InlineKeyboardButton SCHEDULE_ADD_BUTTON = createInlineKeyboardButton("Show/add schedule", "/SCHEDULE");
    public static final InlineKeyboardButton SCHEDULE_HELP_BUTTON = createInlineKeyboardButton("Schedule help", "/SCHEDULE_HELP");
    public static final InlineKeyboardButton SCHEDULE_CLEAR_BUTTON = createInlineKeyboardButton("Clear schedule", "/SCHEDULE_CLEAR");

    public static final List<InlineKeyboardButton> UNAUTHORIZED_ROW = List.of(TOKEN_BUTTON);
    public static final List<InlineKeyboardButton> GET_ROW = List.of(GET_BUTTON);
    public static final List<InlineKeyboardButton> SCHEDULE_ADD_ROW = List.of(SCHEDULE_ADD_BUTTON);
    public static final List<InlineKeyboardButton> SCHEDULE_MANAGE_ROW = List.of(SCHEDULE_CLEAR_BUTTON, SCHEDULE_HELP_BUTTON);

    public static SendMessage createMessageTemplate(User user) {
        return new SendMessage()
                .setChatId(String.valueOf(user.getChatId()))
                .enableMarkdown(true);
    }

    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command) {
        return createInlineKeyboardButton(text, command, false);
    }


    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command, boolean isCommandWithArgs) {
        final String callbackData = isCommandWithArgs ? String.format("%s %s", command, text) : String.format("%s", command);

        return new InlineKeyboardButton()
                .setText(text)
                .setCallbackData(callbackData);
    }

    public static String extractCommand(String text) {
        return text.split(" ")[0];
    }

    public static String extractArguments(String text) {
        return text.substring(text.indexOf(" ") + 1);
    }
}
