package com.whiskels.notifier.reporting;

import com.whiskels.notifier.infrastructure.report.slack.SlackClient;
import com.whiskels.notifier.infrastructure.report.slack.SlackPayloadMapper;
import com.whiskels.notifier.infrastructure.report.slack.SlackReportExecutor;
import com.whiskels.notifier.infrastructure.report.webhook.FeignWebhookSink;
import com.whiskels.notifier.infrastructure.report.webhook.WebhookSinkExecutor;
import com.whiskels.notifier.reporting.service.ReportServiceImpl;
import com.whiskels.notifier.utilities.collections.StreamUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Map;

import static com.whiskels.notifier.utilities.formatters.StringFormatter.COLLECTOR_NEW_LINE;

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
class _ReportConfig {
    private final ReportProperties reportProperties;

    @PostConstruct
    void logWebhookMappings() {
        log.info("Report sink configuration: \n{}", reportProperties.getWebhooks().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> String.format("  %-8s -> %s", entry.getKey(), StreamUtil.map(entry.getValue().entrySet(), Map.Entry::getKey)))
                .collect(COLLECTOR_NEW_LINE));
    }

    @Bean
    ScheduledReportTaskSubmitter scheduledReportTaskSubmitter(TaskScheduler taskScheduler,
                                                              ReportServiceImpl service,
                                                              @Value("${common.timezone}") String timeZone) {
        return new ScheduledReportTaskSubmitter(taskScheduler, service, reportProperties.getSchedule(), timeZone);
    }

    @Bean
    SlackReportExecutor slackPayloadExecutor(SlackClient client, SlackPayloadMapper mapper) {
        return new SlackReportExecutor(client, mapper, reportProperties.getWebhooks().get(WebhookType.SLACK));
    }

    @Bean
    WebhookSinkExecutor webhookSinkExecutor(FeignWebhookSink feignWebhookSink) {
        return new WebhookSinkExecutor(feignWebhookSink, reportProperties.getWebhooks().get(WebhookType.PLAIN));
    }

    @ConfigurationProperties(prefix = "report")
    @Getter
    @Setter
    static class ReportProperties {
        private Map<ReportType, String> schedule;
        private Map<WebhookType, Map<ReportType, String>> webhooks;
    }

    enum WebhookType {
        SLACK,
        PLAIN
    }
}
