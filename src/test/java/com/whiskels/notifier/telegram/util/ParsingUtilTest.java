package com.whiskels.notifier.telegram.util;

import com.whiskels.notifier.telegram.Command;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.whiskels.notifier.telegram.util.ParsingUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParsingUtilTest {
    private static final String TEST_COMMAND = "/GET_DEBT";
    private static final String TEST_ARGUMENTS = "some arguments";
    private static final String TEST_STRING = TEST_COMMAND + " " + TEST_ARGUMENTS;

    @Test
    void oneArgument() {
        List<Integer> time = getTime("1");
        assertEquals(1, time.get(0));
    }

    @Test
    void twoArguments() {
        List<Integer> time = getTime("12");
        assertEquals(12, time.get(0));
    }

    @Test
    void threeArguments() {
        List<Integer> time = getTime("123");
        assertEquals(1, time.get(0));
        assertEquals(23, time.get(1));
    }

    @Test
    void fourArguments() {
        List<Integer> time = getTime("1234");
        assertEquals(12, time.get(0));
        assertEquals(34, time.get(1));
    }

    @Test
    void delimiter() {
        List<Integer> time = getTime("12:34");
        assertEquals(12, time.get(0));
        assertEquals(34, time.get(1));
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
