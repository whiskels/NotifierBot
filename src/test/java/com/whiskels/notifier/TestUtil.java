package com.whiskels.notifier;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertEquals;


@UtilityClass
public class TestUtil {
    public static <T> void assertEqualsIgnoringCR(T o1, T o2) {
        assertEquals(o1.toString().replace("\r",""), o2.toString().replace("\r",""));
    }

    @SneakyThrows
    public static String file(String path) {
        return new ClassPathResource(path).getURL().toString();
    }
}
