package com.whiskels.notifier.infrastructure.admin.telegram.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TelegramUtilTest {

    @Test
    @DisplayName("Should extract arguments from message")
    void shouldExtractArguments() {
        String message = "/command argument1 argument2";
        assertEquals("argument1 argument2", TelegramUtil.extractArguments(message));
    }

    @Test
    @DisplayName("Should extract arguments when")
    void testExtractArgumentsNoSpace() {
        String message = "/command";
        assertEquals(message, TelegramUtil.extractArguments(message));
    }

    @Test
    void testCreateMarkup() {
        var button = new InlineKeyboardButton();
        button.setText("button");
        button.setCallbackData("callbackDate");
        List<List<InlineKeyboardButton>> keyboard = List.of(
                List.of(button),
                List.of(button)
        );
        InlineKeyboardMarkup markup = TelegramUtil.createMarkup(keyboard);
        assertEquals(keyboard, markup.getKeyboard());
    }

    @Test
    void testButton() {
        InlineKeyboardButton button = TelegramUtil.button("text", "callbackData");
        assertEquals("text", button.getText());
        assertEquals("callbackData", button.getCallbackData());
    }
}