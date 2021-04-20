package com.whiskels.notifier.telegram.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collector;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_NEW_LINE;
import static com.whiskels.notifier.common.util.StreamUtil.collectToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportBuilder {
    private final StringBuilder sb = new StringBuilder();
    private String noData = "Nothing";
    private Collector<CharSequence, ?, String> activeCollector = COLLECTOR_NEW_LINE;

    public static ReportBuilder builder(String header) {
        return new ReportBuilder().withHeader(header);
    }

    private ReportBuilder withHeader(String header) {
        sb.append("*")
                .append(header)
                .append("*");
        return line();
    }

    public ReportBuilder setNoData(String noData) {
        this.noData = noData;
        return this;
    }

    public ReportBuilder setActiveCollector(Collector<CharSequence, ?, String> collector) {
        this.activeCollector = collector;
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

    public <T> ReportBuilder list(Collection<T> collection) {
        return list(collection, T::toString);
    }

    public <T> ReportBuilder list(Collection<T> collection, Function<T, String> toString) {
        sb.append(collection.isEmpty() ? noData : collectToString(collection, toString, activeCollector));
        return line();
    }

    public String build() {
        return sb.toString();
    }
}
