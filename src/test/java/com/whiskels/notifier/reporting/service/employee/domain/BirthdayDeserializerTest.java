package com.whiskels.notifier.reporting.service.employee.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.whiskels.notifier.reporting.service.employee.domain.BirthdayDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BirthdayDeserializerTest {
    private final BirthdayDeserializer deserializer = new BirthdayDeserializer();

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;


    @Test
    @DisplayName("Should deserialize using deserializer")
    void testDeserialize() throws IOException {
        String birthdayStr = "20.12";
        LocalDate expectedDate = LocalDate.of(2020, 12, 20);

        when(jsonParser.getText()).thenReturn(birthdayStr);

        LocalDate actualDate = deserializer.deserialize(jsonParser, deserializationContext);

        assertEquals(expectedDate, actualDate);
    }
}