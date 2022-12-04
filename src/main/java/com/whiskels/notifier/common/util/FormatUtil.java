package com.whiskels.notifier.common.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FormatUtil {
    public static final String EMPTY_LINE = "---------------------------";

    public static final Collector<CharSequence, ?, String> COLLECTOR_NEW_LINE = Collectors.joining(String.format(
            "%n"));
    public static final Collector<CharSequence, ?, String> COLLECTOR_TWO_NEW_LINES = Collectors.joining(String.format(
            "%n%n"));
    public static final Collector<CharSequence, ?, String> COLLECTOR_EMPTY_LINE = Collectors.joining(String.format(
            "%n%s%n", EMPTY_LINE));
    public static final Collector<CharSequence, ?, String> COLLECTOR_COMMA_SEPARATED = Collectors.joining(", ");

    public static String format(Number value) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("###,###,###,###.#", formatSymbols);
        return formatter.format(value);
    }
}
