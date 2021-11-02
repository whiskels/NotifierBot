package com.whiskels.notifier.external.employee;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;


public class BirthdayDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String text = jsonParser.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }
        return LocalDate.parse(text, new DateTimeFormatterBuilder()
                .appendPattern("dd.MM")
                .parseDefaulting(ChronoField.YEAR, 2020)
                .toFormatter(Locale.ENGLISH));
    }
}
