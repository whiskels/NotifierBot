package com.whiskels.notifier.telegram.util;

import com.whiskels.notifier.telegram.TelegramLabeled;
import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TelegramUtil {
    public static <T> String getTelegramLabel(T object) {
        Class<?> clazz = object.getClass();
        if (TelegramLabeled.class.isAssignableFrom(clazz)) {
            TelegramLabeled converted = (TelegramLabeled) object;
            return converted.getLabel();
        }
        return clazz.getSimpleName();
    }

    public static <T> boolean isCalledInCallback(T classInstance, String callbackQuery) {
        return getTelegramLabel(classInstance).equals(callbackQuery);
    }

    public static <T> Command getCommandFromClass(T object) {
        return object.getClass().getDeclaredAnnotation(BotCommand.class).command()[0];
    }
}
