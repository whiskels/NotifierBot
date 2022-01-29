package com.whiskels.notifier.external.json;

import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.whiskels.notifier.external.json.JacksonObjectMapper.getMapper;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Lazy
@Component
public class JsonReader {
    private static final String ERROR = "Invalid read array from: %s (%s)";

    public <T> List<T> read(String url, String node, Class<T> clazz) {
        try {
            return readFromString(getMapper()
                            .readTree(new URL(url))
                            .get(node)
                            .toString(), clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(ERROR, url, e));
        }
    }

    public <T> List<T> read(String url, Class<T> clazz) {
        try {
            return readFromString(IOUtils.toString(new URL(url), UTF_8), clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(ERROR, url, e));
        }
    }

    public static <T> List<T> readFromString(String json, Class<T> clazz) {
        try {
            ObjectReader reader = getMapper().readerFor(clazz);
            return reader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(ERROR, json, e));
        }
    }
}
