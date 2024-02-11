package com.whiskels.notifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.opentest4j.AssertionFailedError;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

import static java.nio.charset.StandardCharsets.UTF_8;
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
