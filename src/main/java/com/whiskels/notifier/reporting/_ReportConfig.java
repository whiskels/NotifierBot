package com.whiskels.notifier.reporting;

import com.whiskels.notifier.infrastructure.slack.SlackClient;
import com.whiskels.notifier.reporting.service.ReportServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan
@EnableScheduling
@Slf4j
class _ReportConfig {
    @Bean
    ScheduledReportTaskSubmitter scheduledReportTaskSubmitter(TaskScheduler taskScheduler,
                                                              ReportProperties reportProperties,
                                                              ReportServiceImpl service,
                                                              @Value("${common.timezone}") String timeZone) {
        return new ScheduledReportTaskSubmitter(taskScheduler, service, reportProperties.getSchedule(), timeZone);
    }

    @Bean
    SlackPayloadExecutor slackPayloadExecutor(SlackClient client, ReportProperties reportProperties) {
        return new SlackPayloadExecutor(client, reportProperties.getWebhooks());
    }

    @ConfigurationProperties(prefix = "report")
    @Getter
    @Setter
    static class ReportProperties {
        private Map<ReportType, String> schedule;
        private Map<ReportType, String> webhooks;
    }
}
