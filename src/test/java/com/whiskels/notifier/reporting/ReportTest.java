package com.whiskels.notifier.reporting;

import com.whiskels.notifier.reporting.service.Report;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportTest {

    @Test
    @DisplayName("Should construct SimpleReport via constructor")
    void shouldConstructReport() {
        var header = "Test Header";
        var banner = "Test Banner";
        var body = "Test Body";
        Report report = Report.builder()
                .header(header)
                .notifyChannel(true)
                .banner(banner)
                .build()
                .addBody(body);

        assertEquals(header, report.getHeader());
        assertEquals(banner, report.getBanner());
        assertTrue(report.isNotifyChannel());
        assertThat(report.getBody()).hasSize(1)
                        .allSatisfy(bodyBlock -> {
                            assertEquals(body, bodyBlock.text());
                            assertNull(bodyBlock.mediaContentUrl());
                        });
    }
}
