package com.whiskels.notifier.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FormatUtil {
    public static final String EMPTY_LINE = "---------------------------";
    public static final DateTimeFormatter DAY_MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter YEAR_MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM");

    public static final Collector<CharSequence, ?, String> COLLECTOR_NEW_LINE = Collectors.joining(String.format(
            "%n"));
    public static final Collector<CharSequence, ?, String> COLLECTOR_EMPTY_LINE = Collectors.joining(String.format(
            "%n%s%n", EMPTY_LINE));
    public static final Collector<CharSequence, ?, String> COLLECTOR_COMMA_SEPARATED = Collectors.joining(", ");

    public static String formatDouble(Number value) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("###,###,###,###.#", formatSymbols);
        return formatter.format(value);
    }
}
