package com.whiskels.notifier;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.util.List;

import static com.whiskels.notifier.external.json.JsonReader.readFromString;

@UtilityClass
public class ResourceUtil {
    public static <T> T readJson(String path, Class<T> clazz) {
        List<T> result = readFromString(readFileFromClassPath(path),clazz);
        if (result.size() != 1) {
            throw new IllegalArgumentException("Invalid argument");
        }
        return result.get(0);
    }

    public static <T> List<T> readJsonList(String path, Class<T> clazz) {
        return readFromString(readFileFromClassPath(path),clazz);
    }

    private String readFileFromClassPath(String path) {
        try {
            return new String(
                    Files.readAllBytes(new ClassPathResource(
                            path).getFile().toPath()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
