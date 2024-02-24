package com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.whiskels.notifier.infrastructure.admin.telegram.handler.log.domain.PaperTrailEventLog;
import com.whiskels.notifier.reporting.WireMockTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(properties = {
        "PAPERTRAIL_API_TOKEN=123 ",
        "papertrail.url= http://localhost:9561/logs"
})
@EnableFeignClients(clients = PapertrailClient.class)
@Import({WireMockTestConfig.class})
class PapertrailClientTest {
    private static final String MOCKED_LOGS = """
                {
                  "min_id": "1632503586184237058",
                  "max_id": "1632757144360013837",
                  "events": [
                    {
                      "id": "1632503586184237058",
                      "source_ip": "54.74.219.28",
                      "program": "app/worker.1",
                      "message": "Test log message",
                      "received_at": "2023-08-27T10:13:27-07:00",
                      "generated_at": "2023-08-27T10:13:27-07:00",
                      "display_received_at": "Aug 27 10:13:27",
                      "source_id": 7674447281,
                      "source_name": "telegramnotifierbot",
                      "hostname": "telegramnotifierbot",
                      "severity": "Info",
                      "facility": "Local7"
                    }
                  ],
                  "sawmill": true,
                  "min_time_at": "2023-08-27T10:13:27-07:00",
                  "reached_record_limit": true
                }
                """;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private PapertrailClient papertrailClient;

    @Test
    @DisplayName("Should retrieve log from Papertrail")
    void shouldRetrieveLog() throws Exception {
        mockServer();

        PaperTrailEventLog log = papertrailClient.getLog();
        assertEquals(log, MAPPER.readValue(MOCKED_LOGS, PaperTrailEventLog.class));
    }

    @Test
    @DisplayName("Should retrieve logs from Papertrail")
    void shouldRetrieveLogs() {
        mockServer();

        List<String> logs = papertrailClient.getLogs();
        assertThat(logs).containsExactly("Test log message");
    }

    private void mockServer() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/logs"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("X-Papertrail-Token", "123")
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(MOCKED_LOGS)));
    }
}