package com.whiskels.notifier.utilities.formatters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeFormatter {
    public static final String BIRTHDAY_FORMAT = "dd.MM";
    public static final java.time.format.DateTimeFormatter DAY_MONTH_YEAR_FORMATTER = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final java.time.format.DateTimeFormatter BIRTHDAY_FORMATTER = java.time.format.DateTimeFormatter.ofPattern(BIRTHDAY_FORMAT);
}
