package com.whiskels.telegrambot.util;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static com.whiskels.telegrambot.UserTestData.*;
import static com.whiskels.telegrambot.util.TelegramUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TelegramUtilTest {
    private static final String TEST_COMMAND = "/GET";
    private static final String TEST_ARGUMENTS = "some arguments";
    private static final String TEST_STRING = TEST_COMMAND + " " + TEST_ARGUMENTS;

    @Test
    public void getCommand() {
        assertEquals(TEST_COMMAND, extractCommand(TEST_STRING));
    }

    @Test
    public void getArgs() {
        assertEquals(TEST_ARGUMENTS, extractArguments(TEST_STRING));
    }

    @Test
    public void createButton() {
        InlineKeyboardButton actual = createInlineKeyboardButton(TEST_ARGUMENTS, TEST_COMMAND);
        assertEquals(TEST_COMMAND, actual.getCallbackData());
        assertEquals(TEST_ARGUMENTS, actual.getText());
    }

    @Test
    public void createButtonWithArguments() {
        InlineKeyboardButton actual = createInlineKeyboardButton(TEST_ARGUMENTS, TEST_COMMAND, true);
        assertEquals(TEST_STRING, actual.getCallbackData());
        assertEquals(TEST_ARGUMENTS, actual.getText());
    }

    @Test
    public void messageTemplate() {
        SendMessage actual = createMessageTemplate(ADMIN);
        assertEquals(String.valueOf(ADMIN_CHAT_ID), actual.getChatId());
    }
}
