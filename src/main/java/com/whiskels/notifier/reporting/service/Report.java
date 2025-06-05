package com.whiskels.notifier.reporting.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class Report {
    private final String header;
    @Builder.Default
    private final List<ReportBodyBlock> body = new ArrayList<>();
    private final boolean notifyChannel;
    private final String banner;

    public Report addBody(String text) {
        body.add(ReportBodyBlock.builder().text(text).build());
        return this;
    }

    public Report addBody(String text, String mediaContentUrl) {
        body.add(ReportBodyBlock.builder().text(text).mediaContentUrl(mediaContentUrl).build());
        return this;
    }

    @Builder
    public record ReportBodyBlock(String text, String mediaContentUrl) {
    }
}
