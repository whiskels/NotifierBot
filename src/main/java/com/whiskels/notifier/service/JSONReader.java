package com.whiskels.notifier.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class JSONReader {
    /**
     * Reads all data from reader
     */
    private String readAll(Reader rd) {
        StringBuilder sb = new StringBuilder();
        int cp;
        try {
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
        } catch (IOException | JSONException e) {
            log.error("Exception while trying to read data - {}", e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Reads JSONObject/JSONArray from given URL
     */
    public Object readJsonFromUrl(String url) {
        String value = "";
        try (InputStream is = new URL(url).openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            value = readAll(rd);
            return value.startsWith("[") ? new JSONArray(value) : new JSONObject(value);
        } catch (IOException | JSONException e) {
            log.error("Exception while trying to get JSON data from URL - {}", e.getMessage());
            return null;
        }
    }
}
