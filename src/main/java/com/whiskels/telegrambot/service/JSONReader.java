package com.whiskels.telegrambot.service;

import lombok.extern.slf4j.Slf4j;
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
    private JSONObject readAll(Reader rd) {
        StringBuilder sb = new StringBuilder();
        int cp;
        try {
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
        } catch (IOException e) {
            log.error("Exception while trying to read data - {}", e.getMessage());
        }
        return new JSONObject(sb.toString());
    }


    /**
     * Reads JSONObject from given URL
     */
    public JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            return readAll(rd);
        } catch (IOException | JSONException e) {
            log.error("Exception while trying to get JSON data from URL - {}", e.getMessage());
            return null;
        }
    }
}
