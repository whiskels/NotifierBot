package com.whiskels.notifier.reporting.service.customer.birthday.convert;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import com.whiskels.notifier.utilities.collections.StreamUtil;
import lombok.SneakyThrows;
import org.glassfish.jersey.internal.inject.Custom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerBirthdayInfoReportMessageConverterTest {
    private static final String noData = "No Data";
    private static final String birthdaySubheader = "Happy Birthday!";

    private CustomerBirthdayInfoReportMessageConverter converter;

    @Test
    @DisplayName("Should convert customer payload")
    void shouldConvertCustomerPayload() {
        var expectedOne = """
                {
                  "threadTs" : null,
                  "text" : "Birthday Header",
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Birthday Header",
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
                      "text" : "• John Doe 01.01 (Test company)",
                      "verbatim" : null
                    },
                    "blockId" : "2",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";
        var expectedTwo = """
                {
                  "threadTs" : null,
                  "text" : "Another Birthday Header",
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Another Birthday Header",
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
                      "text" : "No Data",
                      "verbatim" : null
                    },
                    "blockId" : "2",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";
        ReportContext contextOne = mock(ReportContext.class);
        when(contextOne.getSkipEmptyPredicate()).thenReturn(_ -> true);
        when(contextOne.getPredicate()).thenReturn((_, _) -> true);
        when(contextOne.getHeaderMapper()).thenReturn(_ -> "Birthday Header");
        ReportContext contextTwo = mock(ReportContext.class);
        when(contextTwo.getSkipEmptyPredicate()).thenReturn(_ -> false);
        when(contextTwo.getPredicate()).thenReturn((_, _) -> false);
        when(contextTwo.getHeaderMapper()).thenReturn(_ -> "Another Birthday Header");

        var contexts = List.of(contextOne, contextTwo);

        converter = new CustomerBirthdayInfoReportMessageConverter(contexts, noData);

        ReportData<CustomerBirthdayInfo> reportData = new ReportData<>(List.of(
                customer(LocalDate.of(2000, 1, 1))
        ), LocalDate.now(CLOCK));

        Iterable<Payload> payloads = converter.convert(reportData);
        List<Payload> payloadList = (List<Payload>) payloads;

        assertFalse(payloadList.isEmpty());
        assertEquals(2, payloadList.size());
        assertThat(StreamUtil.map(payloadList, this::map))
                .containsExactly(expectedOne, expectedTwo);
    }

    @Test
    @DisplayName("Should return null if skip empty and no data")
    void shouldReturnNullIfSkipEmptyAndNoData() {
        ReportContext context = mock(ReportContext.class);
        when(context.getSkipEmptyPredicate()).thenReturn(_ -> true);
        when(context.getPredicate()).thenReturn((_, _) -> false);

        converter = new CustomerBirthdayInfoReportMessageConverter(singletonList(context), noData);

        ReportData<CustomerBirthdayInfo> reportData = new ReportData<>(emptyList(), LocalDate.now(CLOCK));

        var actual = converter.convert(reportData);

        assertFalse(actual.iterator().hasNext());
    }

    @SneakyThrows
    private String map(Payload payload) {
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
    }

    private CustomerBirthdayInfo customer(LocalDate birthday) {
        return CustomerBirthdayInfo.builder()
                .name("John")
                .surname("Doe")
                .company("Test company")
                .birthday(birthday)
                .build();
    }
}