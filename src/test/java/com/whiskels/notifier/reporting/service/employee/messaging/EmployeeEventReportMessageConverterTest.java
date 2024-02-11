//package com.whiskels.notifier.reporting.service.employee.messaging;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.slack.api.webhook.Payload;
//import com.whiskels.notifier.reporting.service.ReportData;
//import com.whiskels.notifier.reporting.service.employee.domain.Employee;
//import com.whiskels.notifier.reporting.service.employee.convert.EmployeeEventReportMessageConverter;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Stream;
//
//import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
//import static com.whiskels.notifier.TestUtil.assertEqualsIgnoringCR;
//import static java.util.Collections.emptyList;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//public class EmployeeEventReportMessageConverterTest {
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//
//    private final EmployeeEventReportMessageConverter converter = new EmployeeEventReportMessageConverter(
//            "Upcoming employee events",
//            "Nobody",
//            "Birthdays:",
//            "Work anniversaries",
//            CLOCK
//    );
//
//
//    @Test
//    void testNoReport() {
//        LocalDate regularDate = LocalDate.of(2023, 6, 10);
//        ReportData<Employee> data = new ReportData<>(emptyList(), regularDate);
//
//        Iterable<Payload> payload = converter.convert(data);
//
//        assertFalse(payload.iterator().hasNext());
//    }
//
//    @ParameterizedTest
//    @MethodSource("testReportArgs")
//    void testReport(LocalDate reportDate, List<Employee> employees, String report) throws JsonProcessingException {
//        ReportData<Employee> data = new ReportData<>(employees, reportDate);
//
//        Iterable<Payload> iterable = converter.convert(data);
//
//        var iterator = iterable.iterator();
//        assertTrue(iterator.hasNext());
//        var payload = iterator.next();
//        assertEqualsIgnoringCR(report, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
//    }
//
//    static Stream<Arguments> testReportArgs() {
//        return Stream.of(
//                Arguments.of(LocalDate.of(2023, 6, 1), emptyList(), startOfMonthReportJson("Nobody", "Nobody")),
//                Arguments.of(LocalDate.of(2023, 6, 1), List.of(employee("Jane", LocalDate.of(1985, 6, 12), null)), startOfMonthReportJson("• Jane 12.06", "Nobody")),
//                Arguments.of(LocalDate.of(2023, 6, 1), List.of(employee("Jane", null, LocalDate.of(1985, 6, 12))), startOfMonthReportJson("Nobody", "• Jane 12.06 (38)")),
//                Arguments.of(LocalDate.of(2023, 6, 1), List.of(
//                        employee("Jane", LocalDate.of(1985, 1, 12), LocalDate.of(1985, 6, 12)),
//                        employee("John", LocalDate.of(1985, 6, 12), LocalDate.of(1985, 3, 12)),
//                        employee("Jim", LocalDate.of(2000, 6, 12), LocalDate.of(2010, 6, 12))
//
//                ), startOfMonthReportJson("• John 12.06\\n• Jim 12.06", "• Jane 12.06 (38)\\n• Jim 12.06 (13)")),
//                Arguments.of(LocalDate.of(2023, 6, 15), emptyList(), middleOfMonthReport("Nobody", "Nobody")),
//                Arguments.of(LocalDate.of(2023, 6, 15), List.of(employee("Jane", LocalDate.of(1985, 6, 25), null)), middleOfMonthReport("• Jane 25.06", "Nobody")),
//                Arguments.of(LocalDate.of(2023, 6, 15), List.of(employee("Jane", null, LocalDate.of(1985, 6, 25))), middleOfMonthReport("Nobody", "• Jane 25.06 (38)")),
//                Arguments.of(LocalDate.of(2023, 6, 15), List.of(
//                        employee("Jane", LocalDate.of(1985, 1, 25), LocalDate.of(1985, 6, 25)),
//                        employee("John", LocalDate.of(1985, 6, 25), LocalDate.of(1985, 3, 25)),
//                        employee("Jim", LocalDate.of(2000, 6, 25), LocalDate.of(2010, 6, 25)),
//                        employee("Won't show", LocalDate.of(2000, 6, 1), LocalDate.of(2010, 6, 1))
//                ), middleOfMonthReport("• John 25.06\\n• Jim 25.06", "• Jane 25.06 (38)\\n• Jim 25.06 (13)")),
//                Arguments.of(LocalDate.of(2023, 6, 2), List.of(employee("Jane", LocalDate.of(1985, 6, 2), null)), birthdayReportJson("Upcoming employee events on 02-06-2023", "• Jane 02.06" )),
//                Arguments.of(LocalDate.of(2023, 6, 2), List.of(employee("Jane",null,  LocalDate.of(1985, 6, 2))), anniversaryReportJson("Upcoming employee events on 02-06-2023", "• Jane 02.06 (38)" )),
//                Arguments.of(LocalDate.of(2023, 6, 2), List.of(
//                        employee("Jane", LocalDate.of(1985, 1, 25), LocalDate.of(1985, 6, 2)),
//                        employee("John", LocalDate.of(1985, 6, 2), LocalDate.of(1985, 3, 25)),
//                        employee("Jim", LocalDate.of(2000, 6, 2), LocalDate.of(2010, 6, 2))
//
//                ), reportJson("Upcoming employee events on 02-06-2023", "• John 02.06\\n• Jim 02.06", "• Jane 02.06 (38)\\n• Jim 02.06 (13)" ))
//                );
//    }
//
//    private static Employee employee(String name, LocalDate birthdate, LocalDate anniversary) {
//        return new Employee(name, birthdate, anniversary, null, null);
//    }
//
//    private static String onEventJson(String birthdayData, String anniversaryData) {
//        return reportJson("Upcoming employee events today", birthdayData, anniversaryData);
//    }
//
//    private static String startOfMonthReportJson(String birthdayData, String anniversaryData) {
//        return reportJson("Upcoming employee events this month", birthdayData, anniversaryData);
//    }
//
//    private static String middleOfMonthReport(String birthdayData, String anniversaryData) {
//        return reportJson("Upcoming employee events till the end of month", birthdayData, anniversaryData);
//    }
//
//    private static String birthdayReportJson(String header, String birthdayData) {
//        return String.format("""
//                {
//                  "threadTs" : null,
//                  "text" : "%s",
//                  "channel" : null,
//                  "username" : null,
//                  "iconUrl" : null,
//                  "iconEmoji" : null,
//                  "blocks" : [ {
//                    "type" : "header",
//                    "blockId" : "0",
//                    "text" : {
//                      "type" : "plain_text",
//                      "text" : "%s",
//                      "emoji" : false
//                    }
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "@channel",
//                      "verbatim" : null
//                    },
//                    "blockId" : "1",
//                    "fields" : null,
//                    "accessory" : null
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "Birthdays:",
//                      "verbatim" : null
//                    },
//                    "blockId" : "2",
//                    "fields" : null,
//                    "accessory" : null
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "%s",
//                      "verbatim" : null
//                    },
//                    "blockId" : "3",
//                    "fields" : null,
//                    "accessory" : null
//                  } ],
//                  "attachments" : null,
//                  "unfurlLinks" : null,
//                  "unfurlMedia" : null,
//                  "metadata" : null
//                }""", header, header, birthdayData);
//    }
//
//    private static String anniversaryReportJson(String header, String anniversaryData) {
//        return String.format("""
//                {
//                  "threadTs" : null,
//                  "text" : "%s",
//                  "channel" : null,
//                  "username" : null,
//                  "iconUrl" : null,
//                  "iconEmoji" : null,
//                  "blocks" : [ {
//                    "type" : "header",
//                    "blockId" : "0",
//                    "text" : {
//                      "type" : "plain_text",
//                      "text" : "%s",
//                      "emoji" : false
//                    }
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "@channel",
//                      "verbatim" : null
//                    },
//                    "blockId" : "1",
//                    "fields" : null,
//                    "accessory" : null
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "Work anniversaries",
//                      "verbatim" : null
//                    },
//                    "blockId" : "2",
//                    "fields" : null,
//                    "accessory" : null
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "%s",
//                      "verbatim" : null
//                    },
//                    "blockId" : "3",
//                    "fields" : null,
//                    "accessory" : null
//                  } ],
//                  "attachments" : null,
//                  "unfurlLinks" : null,
//                  "unfurlMedia" : null,
//                  "metadata" : null
//                }""", header, header, anniversaryData);
//    }
//
//    private static String reportJson(String header, String birthdayData, String anniversaryData) {
//        return String.format("""
//                {
//                  "threadTs" : null,
//                  "text" : "%s",
//                  "channel" : null,
//                  "username" : null,
//                  "iconUrl" : null,
//                  "iconEmoji" : null,
//                  "blocks" : [ {
//                    "type" : "header",
//                    "blockId" : "0",
//                    "text" : {
//                      "type" : "plain_text",
//                      "text" : "%s",
//                      "emoji" : false
//                    }
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "@channel",
//                      "verbatim" : null
//                    },
//                    "blockId" : "1",
//                    "fields" : null,
//                    "accessory" : null
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "Birthdays:",
//                      "verbatim" : null
//                    },
//                    "blockId" : "2",
//                    "fields" : null,
//                    "accessory" : null
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "%s",
//                      "verbatim" : null
//                    },
//                    "blockId" : "3",
//                    "fields" : null,
//                    "accessory" : null
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "Work anniversaries",
//                      "verbatim" : null
//                    },
//                    "blockId" : "4",
//                    "fields" : null,
//                    "accessory" : null
//                  }, {
//                    "type" : "section",
//                    "text" : {
//                      "type" : "mrkdwn",
//                      "text" : "%s",
//                      "verbatim" : null
//                    },
//                    "blockId" : "5",
//                    "fields" : null,
//                    "accessory" : null
//                  } ],
//                  "attachments" : null,
//                  "unfurlLinks" : null,
//                  "unfurlMedia" : null,
//                  "metadata" : null
//                }""", header, header, birthdayData, anniversaryData);
//    }
//}