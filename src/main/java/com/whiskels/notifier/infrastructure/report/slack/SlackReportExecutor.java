package com.whiskels.notifier.infrastructure.report.slack;

import com.whiskels.notifier.infrastructure.report.ReportExecutor;
import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.service.Report;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@AllArgsConstructor
public class SlackReportExecutor implements ReportExecutor {
    private final SlackClient slackClient;
    private final SlackPayloadMapper slackPayloadMapper;
    private final Map<ReportType, String> webhookMappings;

    @Override
    public void send(ReportType type, Report report) {
        if (isEmpty(webhookMappings) || !webhookMappings.containsKey(type)) {
            log.error("No webhook mapping for type {}", type);
            return;
        }
        try {
            slackClient.send(webhookMappings.get(type), slackPayloadMapper.map(report));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
