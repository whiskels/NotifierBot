package com.whiskels.notifier.common;

import com.fasterxml.jackson.databind.ObjectReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.whiskels.notifier.external.json.JacksonObjectMapper.getMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {
    public static final String ERROR = "Invalid read array from: %s (%s)";

    public static <T> List<T> readValuesFromNode(String url, Class<T> clazz, String node) {
        ObjectReader reader = getMapper().readerFor(clazz);
        try {
            String json = getMapper().readTree(new URL(url)).get(node).toString();
            return reader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(ERROR, url, e));
        }
    }

    public static <T> List<T> readValuesFromUrl(String url, Class<T> clazz) {
        ObjectReader reader = getMapper().readerFor(clazz);
        try {
            return reader.<T>readValues(new URL(url)).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format(ERROR, url, e));
        }
    }

    public static <T> List<T> readValuesFromJson(String json, Class<T> clazz) {
        ObjectReader reader = getMapper().readerFor(clazz);
        try {
            return reader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new  IllegalArgumentException(String.format(ERROR, json, e));
        }
    }
}
