package com.whiskels.notifier.reporting.service.employee.convert;

import com.whiskels.notifier.reporting.service.Report;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        var expectedBirthdayReport = Report.builder()
                .header("Birthday Header")
                .notifyChannel(true)
                .build()
                .addBody("Happy Birthday!")
                .addBody("• Test 01.01")
                .addBody("Happy Anniversary!")
                .addBody("No Data");

        var expectedAnniversary = Report.builder()
                .header("Anniversary Header")
                .notifyChannel(true)
                .build()
                .addBody("Happy Anniversary!")
                .addBody("• Test 01.02 (2)");
        com.whiskels.notifier.reporting.service.employee.convert.ReportContext birthdayContext = mock(com.whiskels.notifier.reporting.service.employee.convert.ReportContext.class);
        when(birthdayContext.getSkipEmpty()).thenReturn(_ -> false);
        when(birthdayContext.getBirthdayPredicate()).thenReturn((_, _) -> true);
        when(birthdayContext.getAnniversaryPredicate()).thenReturn((_, _) -> false);
        when(birthdayContext.getHeaderMapper()).thenReturn(_ -> "Birthday Header");

        com.whiskels.notifier.reporting.service.employee.convert.ReportContext anniversaryContext = mock(com.whiskels.notifier.reporting.service.employee.convert.ReportContext.class);
        when(anniversaryContext.getSkipEmpty()).thenReturn(_ -> true);
        when(anniversaryContext.getBirthdayPredicate()).thenReturn((_, _) -> false);
        when(anniversaryContext.getAnniversaryPredicate()).thenReturn((_, _) -> true);
        when(anniversaryContext.getHeaderMapper()).thenReturn(_ -> "Anniversary Header");

        var contexts = List.of(birthdayContext, anniversaryContext);

        converter = new EmployeeEventReportMessageConverter(contexts, CLOCK, noData, birthdaySubheader, anniversarySubheader);

        ReportData<Employee> reportData = new ReportData<>(List.of(
                employee("Test", LocalDate.of(2000, 1, 1), LocalDate.of(2022, 2, 1))
        ), LocalDate.now(CLOCK));

        List<Report> payloads = (List<Report>) converter.convert(reportData);

        assertFalse(payloads.isEmpty());
        assertEquals(2, payloads.size());
        assertThat(payloads).containsExactly(expectedBirthdayReport, expectedAnniversary);
    }

    @Test
    @DisplayName("Should return null if skip empty and no data")
    void shouldReturnNullIfSkipEmptyAndNoData() {
        com.whiskels.notifier.reporting.service.employee.convert.ReportContext context = mock(com.whiskels.notifier.reporting.service.employee.convert.ReportContext.class);
        when(context.getSkipEmpty()).thenReturn(_ -> true);
        when(context.getBirthdayPredicate()).thenReturn((_, _) -> false);
        when(context.getAnniversaryPredicate()).thenReturn((_, _) -> false);

        converter = new EmployeeEventReportMessageConverter(singletonList(context), CLOCK, noData, birthdaySubheader, anniversarySubheader);

        ReportData<Employee> reportData = new ReportData<>(emptyList(), LocalDate.now(CLOCK));

        var actual = converter.convert(reportData);

        assertFalse(actual.iterator().hasNext());
    }

    private Employee employee(String name, LocalDate birthday, LocalDate appointment) {
        var employee = new Employee();
        employee.setName(name);
        employee.setBirthday(birthday);
        employee.setAppointmentDate(appointment);
        return employee;
    }
}