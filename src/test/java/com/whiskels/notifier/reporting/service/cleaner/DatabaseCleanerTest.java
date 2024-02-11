package com.whiskels.notifier.reporting.service.cleaner;

import com.whiskels.notifier.infrastructure.repository.AbstractRepository;
import com.whiskels.notifier.reporting.service.cleaner.DatabaseCleaner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatabaseCleanerTest {

    @Mock
    private AbstractRepository<?> repo1;

    @Mock
    private AbstractRepository<?> repo2;

    @Mock
    private Clock clock;

    @Test
    @DisplayName("Should delete old entries")
    void shouldDeleteOldEntries() {
        DatabaseCleaner databaseCleaner = new DatabaseCleaner(List.of(repo1, repo2), 1, clock);


        LocalDateTime now = LocalDateTime.of(2023, 8, 19, 9, 0);
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        when(repo1.deleteByDateBefore(any())).thenReturn(5);
        when(repo2.deleteByDateBefore(any())).thenReturn(3);

        databaseCleaner.deleteOldEntries();

        verify(repo1).deleteByDateBefore(any());
        verify(repo2).deleteByDateBefore(any());
    }
}