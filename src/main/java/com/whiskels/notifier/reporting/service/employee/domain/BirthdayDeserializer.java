package com.whiskels.notifier.reporting.service.employee.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;

import static com.whiskels.notifier.utilities.DateTimeUtil.parseDate;


class BirthdayDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return parseDate(jsonParser.getText());
    }
}
