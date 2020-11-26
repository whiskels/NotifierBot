package com.whiskels.notifier.util;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FormatUtil {
    public static final String EMPTY_LINE = "---------------------------";
    public static final DateTimeFormatter DAY_MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter YEAR_MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH);
    public static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM");

    private FormatUtil() {
    }

    public static String extractCommand(String text) {
        return text.split(" ")[0];
    }

    public static String extractArguments(String text) {
        return text.substring(text.indexOf(" ") + 1);
    }

    public static String formatDouble(Number value) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("###,###,###,###.#", formatSymbols);
        return formatter.format(value);
    }
}
