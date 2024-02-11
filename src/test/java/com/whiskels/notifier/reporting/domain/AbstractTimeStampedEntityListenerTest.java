package com.whiskels.notifier.reporting.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractTimeStampedEntityListenerTest {

    @Mock
    private Clock clock;

    @InjectMocks
    private AbstractTimeStampedEntityListener listener;

    @Mock
    private AbstractTimeStampedEntity entity;

    @Test
    @DisplayName("Should set load date time")
    void shouldSetLoadDateTime() {
        LocalDateTime now = LocalDateTime.of(2023, 8, 19, 12, 0);
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        listener.prePersist(entity);

        verify(entity).setLoadDateTime(eq(now));
    }
}