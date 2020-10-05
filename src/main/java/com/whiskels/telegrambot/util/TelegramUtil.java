package com.whiskels.telegrambot.util;

import java.time.format.DateTimeFormatter;

public final class TelegramUtil {
    public static final String EMPTY_LINE = "---------------------------";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static String extractCommand(String text) {
        return text.split(" ")[0];
    }

    public static String extractArguments(String text) {
        return text.substring(text.indexOf(" ") + 1);
    }
}
