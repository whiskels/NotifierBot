package com.whiskels.notifier.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;

import static com.whiskels.notifier.util.FormatUtil.DAY_MONTH_YEAR_FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportBuilder {
    private final StringBuilder sb = new StringBuilder();
    private String noData = "Nothing";

    public static ReportBuilder withHeader(String name, LocalDate date) {
        return new ReportBuilder().header(name, date);
    }

    private ReportBuilder header(String name, LocalDate date) {
        sb.append(String.format("*%s on %s:*%n", name, DAY_MONTH_YEAR_FORMATTER.format(date)));
        return this;
    }

    public ReportBuilder setNoData(String noData) {
        this.noData = noData;
        return this;
    }

    public ReportBuilder line(String line) {
        sb.append(String.format("%n%s%n", line));
        return this;
    }

    public ReportBuilder line() {
        sb.append(String.format("%n"));
        return this;
    }

    public <T> ReportBuilder list(List<T> list, Predicate<T> predicate, Collector<CharSequence, ?, String> collector) {
        final String result = list.stream()
                .filter(predicate)
                .map(T::toString)
                .collect(collector);

        sb.append(result.isEmpty() ? noData : result);
        return this;
    }

    public String build() {
        return sb.toString();
    }
}
