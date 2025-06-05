package com.whiskels.notifier.reporting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ReportPropertiesTest.TestConfig.class)
@TestPropertySource(properties = {
        "report.schedule.EMPLOYEE_EVENT=0 0 12 * * MON-FRI",
        "report.webhooks.SLACK.EMPLOYEE_EVENT=https://webhook.example.com/report1"
})
public class ReportPropertiesTest {

    @Autowired
    private _ReportConfig.ReportProperties reportProperties;

    @Test
    @DisplayName("Should bind properties")
    void shouldBingProperties() {
        Map<ReportType, String> schedule = reportProperties.getSchedule();
        Map<_ReportConfig.WebhookType, Map<ReportType, String>> webhooks = reportProperties.getWebhooks();

        assertEquals("0 0 12 * * MON-FRI", schedule.get(EMPLOYEE_EVENT));
        assertEquals("https://webhook.example.com/report1", webhooks.get(_ReportConfig.WebhookType.SLACK).get(EMPLOYEE_EVENT));
    }

    @Configuration
    @ConfigurationPropertiesScan
    static class TestConfig {
    }
}