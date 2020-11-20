package com.whiskels.notifier.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public final class TelegramUtil {
    public static final String EMPTY_LINE = "---------------------------";
    public static final DateTimeFormatter DATE_YEAR_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH);
    public static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM");

    private TelegramUtil() {}

    public static LocalDate toLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    public static String extractCommand(String text) {
        return text.split(" ")[0];
    }

    public static String extractArguments(String text) {
        return text.substring(text.indexOf(" ") + 1);
    }
}
