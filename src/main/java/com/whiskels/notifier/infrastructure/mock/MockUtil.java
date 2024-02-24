package com.whiskels.notifier.infrastructure.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.InputStream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MockUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    @SneakyThrows
    public static <T> T read(String path, TypeReference<T> ref) {
        return MAPPER.readValue(getResourceFileAsInputStream(path), ref);
    }

    private static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = MockUtil.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
