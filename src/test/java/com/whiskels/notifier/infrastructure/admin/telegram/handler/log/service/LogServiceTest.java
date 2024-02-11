package com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {
    @Mock
    private PapertrailClient papertrailClient;

    @InjectMocks
    private LogService logService;

    @Test
    @DisplayName("Should convert to byte array")
    void shouldConvertToByteArray() {
        when(papertrailClient.getLogs()).thenReturn(List.of("Test"));

        byte[] byteArray = logService.getLogsAsByteArray();

        assertArrayEquals(byteArray, new byte[]{84, 101, 115, 116});
    }
}