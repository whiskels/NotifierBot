package com.whiskels.notifier.util;

import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.whiskels.notifier.util.JacksonObjectMapper.getMapper;

public class JsonUtil {
    public static <T> List<T> readValuesFromNode(String url, Class<T> clazz, String node) {
        ObjectReader reader = getMapper().readerFor(clazz);
        try {
            String json = getMapper().readTree(new URL(url)).get("content").toString();
            return reader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read array from URL:\n'" + url + "'", e);
        }
    }

    public static <T> List<T> readValuesFromUrl(String url, Class<T> clazz) {
        ObjectReader reader = getMapper().readerFor(clazz);
        try {
            return reader.<T>readValues(new URL(url)).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read array from URL:\n'" + url + "'", e);
        }
    }

    public static <T> List<T> readValuesFromJson(String json, Class<T> clazz) {
        ObjectReader reader = getMapper().readerFor(clazz);
        try {
            return reader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read array from URL:\n'" + json + "'", e);
        }
    }
}
