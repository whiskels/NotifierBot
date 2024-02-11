package com.whiskels.notifier.reporting.service.audit;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.service.audit.AuditDataFetchResult;
import com.whiskels.notifier.reporting.service.audit.AuditDataFetchResultAspect;
import com.whiskels.notifier.reporting.service.audit.LoadAudit;
import com.whiskels.notifier.reporting.service.audit.LoadAuditRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditDataFetchResultAspectTest {

    @Mock
    private LoadAuditRepository auditRepository;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Captor
    ArgumentCaptor<LoadAudit> captor;

    @InjectMocks
    private AuditDataFetchResultAspect auditDataFetchResultAspect;

    @ParameterizedTest
    @MethodSource("aroundAdviceTestArgs")
    <T> void aroundAdviceTest(T object, int count) throws Throwable {
        AuditDataFetchResult dataFetchResultAudit = mock(AuditDataFetchResult.class);
        when(dataFetchResultAudit.reportType()).thenReturn(EMPLOYEE_EVENT);
        when(proceedingJoinPoint.proceed()).thenReturn(object);
        when(proceedingJoinPoint.getSignature()).thenReturn(mock(Signature.class));

        Object result = auditDataFetchResultAspect.aroundAdvice(proceedingJoinPoint, dataFetchResultAudit);

        assertEquals(object, result);
        verify(auditRepository).save(captor.capture());
        assertEquals(count, captor.getValue().getCount());
    }

    static Stream<Arguments> aroundAdviceTestArgs() {
        return Stream.of(
                Arguments.of(new ReportData<>(List.of("data1", "data2"), MockedClockConfiguration.EXPECTED_DATE), 2),
                Arguments.of(new Object(), 1),
                Arguments.of(null, 0)
        );
    }
}