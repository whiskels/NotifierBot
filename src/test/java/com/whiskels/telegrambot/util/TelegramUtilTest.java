package com.whiskels.telegrambot.util;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static com.whiskels.telegrambot.UserTestData.ADMIN;
import static com.whiskels.telegrambot.UserTestData.ADMIN_ID;
import static com.whiskels.telegrambot.util.TelegramUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TelegramUtilTest {
    private static final String TEST_COMMAND = "/GET";
    private static final String TEST_ARGUMENTS = "some arguments";
    private static final String TEST_STRING = TEST_COMMAND + " " + TEST_ARGUMENTS;

    @Test
    void getCommand() {
        assertEquals(TEST_COMMAND, extractCommand(TEST_STRING));
    }

    @Test
    void getArgs() {
        assertEquals(TEST_ARGUMENTS, extractArguments(TEST_STRING));
    }

    @Test
    void createButton() {
        InlineKeyboardButton actual = createInlineKeyboardButton(TEST_ARGUMENTS, TEST_COMMAND);
        assertEquals(TEST_COMMAND, actual.getCallbackData());
        assertEquals(TEST_ARGUMENTS, actual.getText());
    }

    @Test
    void createButtonWithArguments() {
        InlineKeyboardButton actual = createInlineKeyboardButton(TEST_ARGUMENTS, TEST_COMMAND, true);
        assertEquals(TEST_STRING, actual.getCallbackData());
        assertEquals(TEST_ARGUMENTS, actual.getText());
    }

    @Test
    void messageTemplate() {
        SendMessage actual = createMessageTemplate(ADMIN);
        assertEquals(String.valueOf(ADMIN_ID), actual.getChatId());
    }
}
