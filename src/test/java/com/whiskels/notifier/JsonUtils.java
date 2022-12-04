package com.whiskels.notifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

import static java.nio.charset.StandardCharsets.UTF_8;

@UtilityClass
public class JsonUtils {
    private static final String ERROR = "Invalid read array from: %s (%s)";
    public static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T> List<T> read(String url, String node, Class<T> clazz) {
        return withTryCatch(url,
                () -> readFromString(MAPPER
                        .readTree(new URL(url))
                        .get(node)
                        .toString(), clazz));
    }

    public static <T> List<T> read(String url, Class<T> clazz) {
        return withTryCatch(url,
                () -> readFromString(IOUtils.toString(new URL(url), UTF_8), clazz));
    }

    public static <T> List<T> readFromString(String json, Class<T> clazz) {
        return withTryCatch(json,
                () -> {
                    ObjectReader reader = MAPPER.readerFor(clazz);
                    return reader.<T>readValues(json).readAll();
                });
    }

    private static <T> List<T> withTryCatch(String source, Callable<List<T>> r) {
        try {
            return r.call();
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(ERROR, source, e));
        }
    }
}
