package com.whiskels.notifier.util;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.whiskels.notifier.util.FormatUtil.DAY_MONTH_YEAR_FORMATTER;
import static com.whiskels.notifier.util.FormatUtil.EMPTY_LINE;

public final class ReportBuilder {
    private final StringBuilder sb = new StringBuilder();

    private ReportBuilder() {
    }

    public static ReportBuilder withHeader(String name, LocalDate date) {
        return new ReportBuilder().header(name, date);
    }

    private ReportBuilder header(String name, LocalDate date) {
        sb.append(String.format("*%s report on %s:*%n", name, DAY_MONTH_YEAR_FORMATTER.format(date)));
        return this;
    }

    public ReportBuilder line(String line) {
        sb.append(String.format("%s%n", line));
        return this;
    }

    public <T> ReportBuilder listWithLine(List<T> list, Predicate<T> predicate) {
        final String result = list.stream()
                .filter(predicate)
                .map(T::toString)
                .collect(Collectors.joining(String.format(
                        "%n%s%n", EMPTY_LINE)));


        sb.append(result.isEmpty() ? "Nothing" : result);
        return this;
    }

    public <T> ReportBuilder list(List<T> list, Predicate<T> predicate) {
        final String result = list.stream()
                .filter(predicate)
                .map(T::toString)
                .collect(Collectors.joining(String.format("%n")));


        sb.append(result.isEmpty() ? "Nothing" : result);
        return this;
    }

    public String build() {
        return sb.toString();
    }
}
