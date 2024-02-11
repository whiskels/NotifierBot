package com.whiskels.notifier.infrastructure.admin.telegram.handler.reload;

import com.whiskels.notifier.infrastructure.admin.telegram.Command;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.audit.LoadAuditRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataReloadHandlerTest {

    @Mock
    private DataFetchService<Integer> dataFetchService;

    @Mock
    private LoadAuditRepository loadAuditRepository;

    private DataReloadHandler dataReloadHandler;

    @BeforeEach
    void setUp() {
        dataReloadHandler = new DataReloadHandler(Collections.singletonList(dataFetchService), loadAuditRepository);
    }

    @Test
    @DisplayName("Should handle message without context with menu")
    void shouldHandleMessageWithoutContextWithMenu() {
        var result = dataReloadHandler.handle("someChatId", "RELOAD_DATA");

        verify(loadAuditRepository).getLast(any());
        verifyNoInteractions(dataFetchService);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle message with context correctly")
    void shouldHandleMessageWithContextCorrectly() {
        when(dataFetchService.fetch()).thenReturn(new ReportData<>(List.of(1,2), null));

        var result = dataReloadHandler.handle("someChatId", STR."RELOAD_DATA \{dataFetchService.getClass().getSimpleName()}");

        verifyNoInteractions(loadAuditRepository);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should return correct command for reload handler")
    void shouldReturnCorrectCommandForReloadHandler() {
        assertEquals(Command.RELOAD_DATA, dataReloadHandler.getCommand());
    }
}