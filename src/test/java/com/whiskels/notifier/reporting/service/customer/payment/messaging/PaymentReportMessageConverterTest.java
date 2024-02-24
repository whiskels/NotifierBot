package com.whiskels.notifier.reporting.service.customer.payment.messaging;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.payment.domain.CustomerPaymentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static com.whiskels.notifier.TestUtil.assertEqualsIgnoringCR;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentReportMessageConverterTest {
    private final PaymentReportMessageConverter converter = new PaymentReportMessageConverter(
            "Payment report on",
            "Nothing",
            Map.of(0, singletonList("Test"), 100000, singletonList("Test2"))
    );

    @Test
    @DisplayName("Should prepare empty report")
    void shouldPrepareEmptyReport() throws Exception {
        LocalDate regularDate = LocalDate.of(2023, 6, 10);
        ReportData<CustomerPaymentDto> data = new ReportData<>(emptyList(), regularDate);

        Iterable<Payload> iterable = converter.convert(data);

        var iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        var payload = iterator.next();
        assertEqualsIgnoringCR(
                """
                        {
                          "threadTs" : null,
                          "text" : "Payment report on 10-06-2023",
                          "channel" : null,
                          "username" : null,
                          "iconUrl" : null,
                          "iconEmoji" : null,
                          "blocks" : [ {
                            "type" : "header",
                            "blockId" : "0",
                            "text" : {
                              "type" : "plain_text",
                              "text" : "Payment report on 10-06-2023",
                              "emoji" : false
                            }
                          }, {
                            "type" : "section",
                            "text" : {
                              "type" : "mrkdwn",
                              "text" : "@channel",
                              "verbatim" : null
                            },
                            "blockId" : "1",
                            "fields" : null,
                            "accessory" : null
                          }, {
                            "type" : "section",
                            "text" : {
                              "type" : "mrkdwn",
                              "text" : "Nothing",
                              "verbatim" : null
                            },
                            "blockId" : "2",
                            "fields" : null,
                            "accessory" : {
                              "type" : "image",
                              "image_url" : "Test",
                              "alt_text" : "Funny pic"
                            }
                          } ],
                          "attachments" : null,
                          "unfurlLinks" : null,
                          "unfurlMedia" : null,
                          "metadata" : null
                        }""", MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
    }

    @Test
    @DisplayName("Should prepare report")
    void shouldPrepareReport() throws Exception {
        LocalDate regularDate = LocalDate.of(2023, 6, 10);
        CustomerPaymentDto dtoOne = CustomerPaymentDto.builder()
                .amount(new BigDecimal("100000"))
                .amountRub(new BigDecimal("10000000"))
                .currency("EUR")
                .contractor("First entry")
                .build();
        CustomerPaymentDto dtoTwo = CustomerPaymentDto.builder()
                .amount(new BigDecimal("100000000"))
                .amountRub(new BigDecimal("100000000"))
                .currency("RUB")
                .contractor("Second Entry entry")
                .build();
        ReportData<CustomerPaymentDto> data = new ReportData<>(List.of(dtoOne, dtoTwo), regularDate);

        Iterable<Payload> iterable = converter.convert(data);

        var iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        var payload = iterator.next();
        assertEqualsIgnoringCR("""
                {
                  "threadTs" : null,
                  "text" : "Payment report on 10-06-2023",
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Payment report on 10-06-2023",
                      "emoji" : false
                    }
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "@channel",
                      "verbatim" : null
                    },
                    "blockId" : "1",
                    "fields" : null,
                    "accessory" : null
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "Second Entry entry — 100 000 000 RUB\\nFirst entry — 100 000 EUR",
                      "verbatim" : null
                    },
                    "blockId" : "2",
                    "fields" : null,
                    "accessory" : {
                      "type" : "image",
                      "image_url" : "Test2",
                      "alt_text" : "Funny pic"
                    }
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""", MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
    }
}