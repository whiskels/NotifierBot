package com.whiskels.notifier.util;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public static String reportHeader(String name, LocalDateTime date) {
        return String.format("*%s report on %s:*%n", name, DAY_MONTH_YEAR_FORMATTER.format(date));
    }

    public static <T> String formatListWithEmptyLine(List<T> list, Predicate<T> predicate) {
        final String result = list.stream()
                .filter(predicate)
                .map(T::toString)
                .collect(Collectors.joining(String.format(
                        "%n%s%n", EMPTY_LINE)));


        return result.isEmpty() ? "Nothing" : result;
    }

    public static <T> String formatList(List<T> list, Predicate<T> predicate) {
        final String result = list.stream()
                .filter(predicate)
                .map(T::toString)
                .collect(Collectors.joining(String.format("%n")));


        return result.isEmpty() ? "Nothing" : result;
    }
}
