package com.whiskels.notifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UtilityClass
public class JsonUtils {
    public static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    @SuppressWarnings("unchecked")
    public static <T> void assertEqualsWithJson(String expected, T actual) {
        T expectedObject;
        try {
            expectedObject = MAPPER.readValue(expected, (Class<T>) actual.getClass());
        } catch (Exception e) {
            throw new IllegalStateException(STR."Failed to read json \{expected}");
        }
        assertEquals(expectedObject, actual);
    }
}
