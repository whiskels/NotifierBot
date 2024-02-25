package com.whiskels.notifier.utilities.formatters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeFormatter {
    public static final String BIRTHDAY_FORMAT = "dd.MM";
    public static final java.time.format.DateTimeFormatter DAY_MONTH_YEAR_FORMATTER = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final java.time.format.DateTimeFormatter BIRTHDAY_FORMATTER = java.time.format.DateTimeFormatter.ofPattern(BIRTHDAY_FORMAT);

    public static java.time.format.DateTimeFormatter FORMATTER_WITH_YEAR = new DateTimeFormatterBuilder()
            .appendPattern("[dd.MM.yyyy]")
            .appendPattern("[dd.M.yyyy]")
            .appendPattern("[dd/MM/yyyy]")
            .toFormatter(Locale.ENGLISH);

    public static java.time.format.DateTimeFormatter FORMATTER_WITHOUT_YEAR = new DateTimeFormatterBuilder()
            .appendPattern("[dd.MM]")
            .appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').toFormatter())
            .appendPattern("[dd/MM]")
            .appendOptional(new DateTimeFormatterBuilder().appendLiteral('/').toFormatter())
            .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
            .toFormatter(Locale.ENGLISH);

}
