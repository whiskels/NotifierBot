package com.whiskels.notifier.telegram.util;

import com.whiskels.notifier.telegram.Command;
import org.junit.jupiter.api.Test;

import static com.whiskels.notifier.telegram.util.ParsingUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParsingUtilTest {
    private static final String TEST_COMMAND = "/GET_DEBT";
    private static final String TEST_ARGUMENTS = "some arguments";
    private static final String TEST_STRING = TEST_COMMAND + " " + TEST_ARGUMENTS;

    @Test
    void oneArgument() {
        var time = getTime("1");
        assertEquals(1, time.getHours());
        assertEquals(0, time.getMinutes());
    }

    @Test
    void twoArguments() {
        var time = getTime("12");
        assertEquals(12, time.getHours());
        assertEquals(0, time.getMinutes());
    }

    @Test
    void threeArguments() {
        var time = getTime("123");
        assertEquals(1, time.getHours());
        assertEquals(23, time.getMinutes());
    }

    @Test
    void fourArguments() {
        var time = getTime("1234");
        assertEquals(12, time.getHours());
        assertEquals(34, time.getMinutes());
    }

    @Test
    void delimiter() {
        var time = getTime("12:34");
        assertEquals(12, time.getHours());
        assertEquals(34, time.getMinutes());
    }

    @Test
    void oneArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> getTime("a"));
    }

    @Test
    void invalidTime() {
        assertThrows(IllegalArgumentException.class, () -> getTime("4260"));
    }

    @Test
    void tooManyArguments() {
        assertThrows(IllegalArgumentException.class, () -> getTime("10000"));
    }

    @Test
    void invalidDelimiter() {
        assertThrows(IllegalArgumentException.class, () -> getTime("12-00"));
    }

    @Test
    void getCommand() {
        assertEquals(Command.GET_DEBT, extractCommand(TEST_STRING));
    }

    @Test
    void getArgs() {
        assertEquals(TEST_ARGUMENTS, extractArguments(TEST_STRING));
    }
}
