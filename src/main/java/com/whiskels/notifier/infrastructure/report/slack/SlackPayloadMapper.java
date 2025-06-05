package com.whiskels.notifier.infrastructure.report.slack;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.infrastructure.report.slack.builder.SlackPayloadBuilder;
import com.whiskels.notifier.reporting.service.Report;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
public class SlackPayloadMapper {
    public Payload map(final Report report) {
        var builder = SlackPayloadBuilder.builder()
                .header(report.getHeader());
        if (report.isNotifyChannel()) {
            builder.notifyChannel();
        }

        if (nonNull(report.getBanner())) {
            builder.header(report.getBanner());
        }

        report.getBody().forEach(block -> {
            if (nonNull(block.mediaContentUrl())) {
                builder.block(block.text(), block.mediaContentUrl());
            } else {
                builder.block(block.text());
            }
        });

        return builder.build();
    }
}
