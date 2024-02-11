package com.whiskels.notifier.reporting;

import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.webhook.Payload;
import com.whiskels.notifier.reporting.service.SimpleReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleReportTest {

    @Test
    @DisplayName("Should construct SimpleReport via constructor")
    void shouldConstructSimpleReport() {
        SimpleReport report = new SimpleReport("Test Header", "Test Body");

        assertEquals("Test Header", report.header());
        assertEquals("Test Body", report.body());
    }

    @Test
    @DisplayName("Should convert Simple Report to Slack Payload")
    void shouldConvertToSlackPayload() {
        SimpleReport report = new SimpleReport("Test Header", "Test Body");
        Payload payload = report.toSlackPayload();

        assertNotNull(payload);
        assertEquals(payload.getText(), "Test Header");
        assertEquals(((HeaderBlock) payload.getBlocks().get(0)).getText().getText(), "Test Header");
        assertEquals(((SectionBlock) payload.getBlocks().get(1)).getText().getText(), "@channel");
        assertEquals(((SectionBlock) payload.getBlocks().get(2)).getText().getText(), "Test Body");
    }
}
