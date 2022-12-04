package com.whiskels.notifier.common.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_DATE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditCleanerTest {
    @SuppressWarnings("unchecked")
    private final AuditRepository<AbstractAuditedEntity> mockRepo =
            Mockito.mock(AuditRepository.class);
    private final AuditCleaner auditCleaner =
            new AuditCleaner(Collections.singletonList(mockRepo), 1, CLOCK);

    @Test
    void deleteOldEntries() {
        var expectedDate = EXPECTED_DATE.minusDays(1L);
        when(mockRepo.deleteByDateBefore(expectedDate)).thenReturn(1);
        when(mockRepo.getLabel()).thenReturn("label");

        auditCleaner.deleteOldEntries();

        verify(mockRepo).deleteByDateBefore(expectedDate);
        verify(mockRepo).getLabel();
        verifyNoMoreInteractions(mockRepo);
    }
}