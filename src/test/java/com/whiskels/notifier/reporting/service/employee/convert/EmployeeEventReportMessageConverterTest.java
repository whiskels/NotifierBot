package com.whiskels.notifier.reporting.service.employee.convert;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import com.whiskels.notifier.utilities.collections.StreamUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmployeeEventReportMessageConverterTest {

    private static final String noData = "No Data";
    private static final String birthdaySubheader = "Happy Birthday!";
    private static final String anniversarySubheader = "Happy Anniversary!";

    private EmployeeEventReportMessageConverter converter;

    @Test
    @DisplayName("Should convert employee payloads")
    void shouldConvertEmployeePayload() {
        var expectedBirthday = """
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
                      "text" : "Happy Birthday!",
                      "verbatim" : null
                    },
                    "blockId" : "2",
                    "fields" : null,
                    "accessory" : null
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "• Test 01.01",
                      "verbatim" : null
                    },
                    "blockId" : "3",
                    "fields" : null,
                    "accessory" : null
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "Happy Anniversary!",
                      "verbatim" : null
                    },
                    "blockId" : "4",
                    "fields" : null,
                    "accessory" : null
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "No Data",
                      "verbatim" : null
                    },
                    "blockId" : "5",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";
        var expectedAnniversary = """
                {
                  "threadTs" : null,
                  "text" : "Anniversary Header",
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Anniversary Header",
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
                      "text" : "Happy Anniversary!",
                      "verbatim" : null
                    },
                    "blockId" : "2",
                    "fields" : null,
                    "accessory" : null
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "• Test 01.02 (2)",
                      "verbatim" : null
                    },
                    "blockId" : "3",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";
        ReportContext birthdayContext = mock(ReportContext.class);
        when(birthdayContext.getSkipEmpty()).thenReturn(_ -> false);
        when(birthdayContext.getBirthdayPredicate()).thenReturn((_, _) -> true);
        when(birthdayContext.getAnniversaryPredicate()).thenReturn((_, _) -> false);
        when(birthdayContext.getHeaderMapper()).thenReturn(_ -> "Birthday Header");

        ReportContext anniversaryContext = mock(ReportContext.class);
        when(anniversaryContext.getSkipEmpty()).thenReturn(_ -> true);
        when(anniversaryContext.getBirthdayPredicate()).thenReturn((_, _) -> false);
        when(anniversaryContext.getAnniversaryPredicate()).thenReturn((_, _) -> true);
        when(anniversaryContext.getHeaderMapper()).thenReturn(_ -> "Anniversary Header");

        var contexts = List.of(birthdayContext, anniversaryContext);

        converter = new EmployeeEventReportMessageConverter(contexts, CLOCK, noData, birthdaySubheader, anniversarySubheader);

        ReportData<Employee> reportData = new ReportData<>(List.of(
                employee("Test", LocalDate.of(2000, 1, 1), LocalDate.of(2022, 2, 1))
        ), LocalDate.now(CLOCK));

        Iterable<Payload> payloads = converter.convert(reportData);
        List<Payload> payloadList = (List<Payload>) payloads;

        assertFalse(payloadList.isEmpty());
        assertEquals(2, payloadList.size());
        assertThat(StreamUtil.map(payloadList, this::map))
                .containsExactly(expectedBirthday, expectedAnniversary);
    }

    @Test
    @DisplayName("Should return null if skip empty and no data")
    void shouldReturnNullIfSkipEmptyAndNoData() {
        ReportContext context = mock(ReportContext.class);
        when(context.getSkipEmpty()).thenReturn(_ -> true);
        when(context.getBirthdayPredicate()).thenReturn((_, _) -> false);
        when(context.getAnniversaryPredicate()).thenReturn((_, _) -> false);

        converter = new EmployeeEventReportMessageConverter(singletonList(context), CLOCK, noData, birthdaySubheader, anniversarySubheader);

        ReportData<Employee> reportData = new ReportData<>(emptyList(), LocalDate.now(CLOCK));

        var actual = converter.convert(reportData);

        assertFalse(actual.iterator().hasNext());
    }

    @SneakyThrows
    private String map(Payload payload) {
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
    }

    private Employee employee(String name, LocalDate birthday, LocalDate appointment) {
        var employee = new Employee();
        employee.setName(name);
        employee.setBirthday(birthday);
        employee.setAppointmentDate(appointment);
        return employee;
    }
}