package com.whiskels.notifier.utilities.formatters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringFormatterTest {
    @Test
    @DisplayName("Should collect string with new lines")
    void shouldCollectStringWithNewLine() {
        String joined = Stream.of("Hello", "World")
                .collect(StringFormatter.COLLECTOR_NEW_LINE);

        assertEquals(String.format("Hello%nWorld"), joined);
    }

    @Test
    @DisplayName("Should format number correctly")
    void shouldFormatNumberCorrectly() {
        String formatted = StringFormatter.format(1234567890.5678);

        assertEquals("1 234 567 890.6", formatted); // Rounded to 1 decimal place
    }

    @Test
    @DisplayName("Should format number without decimal part correctly")
    void shouldFormatNumberWithoutDecimalCorrectly() {
        String formatted = StringFormatter.format(1234567890);

        assertEquals("1 234 567 890", formatted);
    }
}