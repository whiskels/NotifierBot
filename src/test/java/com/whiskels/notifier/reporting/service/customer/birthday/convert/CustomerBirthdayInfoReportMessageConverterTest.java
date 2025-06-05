package com.whiskels.notifier.reporting.service.customer.birthday.convert;

import com.whiskels.notifier.reporting.service.Report;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
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

class CustomerBirthdayInfoReportMessageConverterTest {
    private static final String noData = "No Data";

    private CustomerBirthdayInfoReportMessageConverter converter;

    @Test
    @DisplayName("Should convert customer payload")
    void shouldConvertCustomerPayload() {
        var birthdayHeader = "Birthday Header";
        var expectedOne = Report.builder()
                .header(birthdayHeader)
                .notifyChannel(false)
                .build().addBody("• John Doe 01.01 (Test company)");
        var birthdayHeaderTwo = "Another Birthday Header";
        var expectedTwo = Report.builder()
                .header(birthdayHeaderTwo)
                .notifyChannel(false)
                .build().addBody("No Data");
        ReportContext contextOne = mock(ReportContext.class);
        when(contextOne.getSkipEmptyPredicate()).thenReturn(_ -> true);
        when(contextOne.getPredicate()).thenReturn((_, _) -> true);
        when(contextOne.getHeaderMapper()).thenReturn(_ -> birthdayHeader);
        ReportContext contextTwo = mock(ReportContext.class);
        when(contextTwo.getSkipEmptyPredicate()).thenReturn(_ -> false);
        when(contextTwo.getPredicate()).thenReturn((_, _) -> false);
        when(contextTwo.getHeaderMapper()).thenReturn(_ -> birthdayHeaderTwo);

        var contexts = List.of(contextOne, contextTwo);

        converter = new CustomerBirthdayInfoReportMessageConverter(contexts, noData);

        ReportData<CustomerBirthdayInfo> reportData = new ReportData<>(List.of(
                customer(LocalDate.of(2000, 1, 1))
        ), LocalDate.now(CLOCK));

        List<Report> reports = (List<Report>) converter.convert(reportData);

        assertFalse(reports.isEmpty());
        assertEquals(2, reports.size());
        assertThat(reports).containsExactly(expectedOne, expectedTwo);
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

    private CustomerBirthdayInfo customer(LocalDate birthday) {
        return CustomerBirthdayInfo.builder()
                .name("John")
                .surname("Doe")
                .company("Test company")
                .birthday(birthday)
                .build();
    }
}