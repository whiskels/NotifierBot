package com.whiskels.notifier.external.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whiskels.notifier.external.json.employee.BirthdayDeserializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BirthdayDeserializerTest {
    private ObjectMapper mapper;
    private JsonDeserializer<LocalDate> deserializer;

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
        deserializer = new BirthdayDeserializer();
    }

    @Test
    void testDeserialize_notNull() {
        String json = String.format("{\"date\":%s}", "\"01.01\"");
        LocalDate deserialisedDate = deserialize(json);
        assertThat(deserialisedDate, instanceOf(LocalDate.class));
        assertEquals(LocalDate.of(2020,1,1), deserialisedDate);
    }

    @Test
    void testDeserialize_null() {
        String json = String.format("{\"date\":%s}", "\"\"");
        assertNull(deserialize(json));
    }

    @SneakyThrows({JsonParseException.class, IOException.class})
    private LocalDate deserialize(String json) {
        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        JsonParser parser = mapper.getFactory().createParser(stream);
        DeserializationContext ctxt = mapper.getDeserializationContext();
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        return deserializer.deserialize(parser, ctxt);
    }
}