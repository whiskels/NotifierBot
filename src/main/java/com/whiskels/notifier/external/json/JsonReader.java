package com.whiskels.notifier.external.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.whiskels.notifier.external.ExternalApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.whiskels.notifier.external.json.JacksonObjectMapper.getMapper;

@Component
@Slf4j
@ConditionalOnBean(ExternalApiClient.class)
public class JsonReader {
    private static final String ERROR = "Invalid read array from: %s (%s)";

    public <T> List<T> read(String url, String node, Class<T> clazz) {
        try {
            final ObjectMapper mapper = getMapper();
            final String json = mapper.readTree(new URL(url)).get(node).toString();
            return mapper.readerFor(clazz).<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(ERROR, url, e));
        }
    }

    public <T> List<T> read(String url, Class<T> clazz) {
        try {
            ObjectReader reader = getMapper().readerFor(clazz);
            return reader.<T>readValues(new URL(url)).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(ERROR, url, e));
        }
    }

    public <T> List<T> readFromString(String json, Class<T> clazz) {
        try {
            ObjectReader reader = getMapper().readerFor(clazz);
            return reader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(ERROR, json, e));
        }
    }
}
