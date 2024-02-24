package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.whiskels.notifier.reporting.service.audit.LoadAuditRepository;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentReportDataFetchServiceTest {
    @Mock
    private FinOperationRepository repository;
    @Mock
    private LoadAuditRepository auditRepository;

    @InjectMocks
    private PaymentReportDataFetchService paymentReportDataFetchService;

    @Test
    @DisplayName("Should fetch payment data")
    void shouldFetchPaymentData() {
        var expectedDateTime = LocalDateTime.now(CLOCK);
        when(auditRepository.findLastUpdateDateTime(any()))
                .thenReturn(Optional.of(expectedDateTime));
        when(repository.getAllByCategoryAndDateBetween(
                "Revenue", expectedDateTime.with(LocalTime.MIN), expectedDateTime.with(LocalTime.MAX))
        ).thenReturn(List.of(new FinancialOperation()));

        var actual = paymentReportDataFetchService.fetch();

        assertEquals(expectedDateTime.toLocalDate(), actual.requestDate());
        assertEquals(1, actual.data().size());
    }

    @Test
    @DisplayName("Should return empty payment data when no last update date time")
    void shouldFetchEmptyPaymentData() {
        when(auditRepository.findLastUpdateDateTime(any()))
                .thenReturn(Optional.empty());

        var actual = paymentReportDataFetchService.fetch();

        verifyNoInteractions(repository);
        assertEquals(LocalDate.now(), actual.requestDate());
        assertEquals(0, actual.data().size());
    }
}