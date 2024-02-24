package com.whiskels.notifier.utilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UtilTest {
    @Test
    @DisplayName("Should return value if it is not null")
    void shouldReturnValueWhenArgumentIsNotNull() {
        String result = Util.defaultIfNull("Hello", "Default");
        assertEquals("Hello", result);
    }

    @Test
    @DisplayName("Should return default value if argument is null")
    void shouldReturnDefaultValueWhenArgumentIsNull() {
        String result = Util.defaultIfNull(null, "Default");
        assertEquals("Default", result);
    }

    @Test
    @DisplayName("Should return null if both values are null")
    void shouldReturnNullWhenBothValuesAreNull() {
        String result = Util.defaultIfNull(null, null);
        assertNull(result);
    }
}