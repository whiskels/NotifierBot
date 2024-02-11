package com.whiskels.notifier.infrastructure.admin.telegram.util;

import com.whiskels.notifier.infrastructure.admin.telegram.BotMessage;
import com.whiskels.notifier.infrastructure.admin.telegram.DocumentBotMessage;
import com.whiskels.notifier.infrastructure.admin.telegram.TextBotMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static java.util.Collections.singleton;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TelegramUtil {
    public static String extractArguments(String message) {
        return message.substring(message.indexOf(" ") + 1);
    }

    public static InlineKeyboardMarkup createMarkup(List<List<InlineKeyboardButton>> keyboard) {
        var markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public static InlineKeyboardButton button(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

}
