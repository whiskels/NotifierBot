package com.whiskels.notifier.infrastructure.slack.builder;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.infrastructure.report.slack.builder.SlackPayloadBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static com.whiskels.notifier.TestUtil.assertEqualsIgnoringCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SlackPayloadBuilderTest {
    @Test
    @DisplayName("Should initialize builder")
    void testBuilder() {
        SlackPayloadBuilder builder = SlackPayloadBuilder.builder();
        assertNotNull(builder);
    }

    @Test
    @DisplayName("Should map notification message")
    void testNotificationMessage() {
        SlackPayloadBuilder builder = SlackPayloadBuilder.builder().notificationMessage("Hello");
        Payload payload = builder.build();
        assertEquals("Hello", payload.getText());
    }

    @Test
    @DisplayName("Should map header message")
    void testHeaderWithNotification() throws Exception {
        final var expected = """
                {
                  "threadTs" : null,
                  "text" : "Header",
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Header",
                      "emoji" : false
                    }
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";

        SlackPayloadBuilder builder = SlackPayloadBuilder.builder().header("Header", true);
        Payload payload = builder.build();

        assertEqualsIgnoringCR(expected, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
    }

    @Test
    @DisplayName("Should map header without message")
    void testHeaderWithoutNotification() throws Exception {
        final var expected = """
                {
                  "threadTs" : null,
                  "text" : null,
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Header",
                      "emoji" : false
                    }
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";

        SlackPayloadBuilder builder = SlackPayloadBuilder.builder().header("Header", false);
        Payload payload = builder.build();

        assertEqualsIgnoringCR(expected, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
    }

    @Test
    @DisplayName("Should not override header")
    void testHeaderTwoCalls() throws Exception {
        final var expected = """
                {
                  "threadTs" : null,
                  "text" : null,
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Header",
                      "emoji" : false
                    }
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";

        SlackPayloadBuilder builder = SlackPayloadBuilder.builder()
                .header("Header", false)
                .header("Second Header", false);
        Payload payload = builder.build();

        assertEqualsIgnoringCR(expected, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
    }

    @Test
    @DisplayName("Should add channel notification")
    void testNotifyChannel() throws Exception {
        final var expected = """
                {
                  "threadTs" : null,
                  "text" : null,
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "@channel",
                      "verbatim" : null
                    },
                    "blockId" : "1",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";

        SlackPayloadBuilder builder = SlackPayloadBuilder.builder().notifyChannel();
        Payload payload = builder.build();

        assertEqualsIgnoringCR(expected, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
    }

    @Test
    @DisplayName("Should add only text block")
    void testBlockWithTextOnly() throws Exception {
        final var expected = """
                {
                  "threadTs" : null,
                  "text" : null,
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "Text",
                      "verbatim" : null
                    },
                    "blockId" : "1",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";

        SlackPayloadBuilder builder = SlackPayloadBuilder.builder().block("Text");
        Payload payload = builder.build();

        assertEqualsIgnoringCR(expected, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
    }

    @Test
    @DisplayName("Should add text block and picture")
    void testBlockWithTextAndPic() throws Exception {
        final var expected = """
                {
                  "threadTs" : null,
                  "text" : null,
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "Text",
                      "verbatim" : null
                    },
                    "blockId" : "1",
                    "fields" : null,
                    "accessory" : {
                      "type" : "image",
                      "image_url" : "pic_url",
                      "alt_text" : "Funny pic"
                    }
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";

        SlackPayloadBuilder builder = SlackPayloadBuilder.builder().block("Text", "pic_url");
        Payload payload = builder.build();

        assertEqualsIgnoringCR(expected, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
    }
}