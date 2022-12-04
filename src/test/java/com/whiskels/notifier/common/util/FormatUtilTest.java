package com.whiskels.notifier.common.util;

import org.junit.jupiter.api.Test;

import static com.whiskels.notifier.common.util.FormatUtil.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatUtilTest {
    @Test
    void testFormatDouble() {
        assertEquals("123 456 789.1", format(123456789.12345d));
        assertEquals("12 345 678 912 345.1", format(12345678912345.12345d));
        assertEquals("2", format(2.0d));
        assertEquals("9 000", format(9000.0d));
    }
}
