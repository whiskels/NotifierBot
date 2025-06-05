package com.whiskels.notifier.infrastructure.report.webhook;

import com.whiskels.notifier.infrastructure.report.ReportExecutor;
import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.service.Report;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.Map;

import static com.whiskels.notifier.utilities.formatters.StringFormatter.COLLECTOR_NEW_LINE;

@Slf4j
@RequiredArgsConstructor
public class WebhookSinkExecutor implements ReportExecutor {
    private final FeignWebhookSink feignWebhookSink;
    private final Map<ReportType, String> webhookMappings;

    @Override
    public void send(final ReportType type, final Report report) {
        if (CollectionUtils.isEmpty(webhookMappings) || !webhookMappings.containsKey(type)) {
            log.error("No webhook mapping for type {}", type);
            return;
        }
        try {
            final var dto = new FeignWebhookSinkDto();
            var reportBody = report.getBody().stream()
                    .map(Report.ReportBodyBlock::text)
                    .collect(COLLECTOR_NEW_LINE);
            dto.setMessage(STR."\{report.getHeader()}\n\n\{reportBody}");
            final var response = feignWebhookSink.send(new URI(webhookMappings.get(type)), dto);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Webhook sink {} failed: {}", type, response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
