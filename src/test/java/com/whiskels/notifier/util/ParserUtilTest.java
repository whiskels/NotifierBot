package com.whiskels.notifier.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.whiskels.notifier.util.ParsingUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserUtilTest {
    private static final String TEST_COMMAND = "/GET";
    private static final String TEST_ARGUMENTS = "some arguments";
    private static final String TEST_STRING = TEST_COMMAND + " " + TEST_ARGUMENTS;

    @Test
    public void oneArgument() {
        List<Integer> time = getTime("1");
        assertEquals(1, time.get(0));
    }

    @Test
    public void twoArguments() {
        List<Integer> time = getTime("12");
        assertEquals(12, time.get(0));
    }

    @Test
    public void threeArguments() {
        List<Integer> time = getTime("123");
        assertEquals(1, time.get(0));
        assertEquals(23, time.get(1));
    }

    @Test
    public void fourArguments() {
        List<Integer> time = getTime("1234");
        assertEquals(12, time.get(0));
        assertEquals(34, time.get(1));
    }

    @Test
    public void delimiter() {
        List<Integer> time = getTime("12:34");
        assertEquals(12, time.get(0));
        assertEquals(34, time.get(1));
    }

    @Test
    public void oneArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> getTime("a"));
    }

    @Test
    public void invalidTime() {
        assertThrows(IllegalArgumentException.class, () -> getTime("4260"));
    }

    @Test
    public void tooManyArguments() {
        assertThrows(IllegalArgumentException.class, () -> getTime("10000"));
    }

    @Test
    public void invalidDelimiter() {
        assertThrows(IllegalArgumentException.class, () -> getTime("12-00"));
    }

    @Test
    public void getCommand() {
        assertEquals(TEST_COMMAND, extractCommand(TEST_STRING));
    }

    @Test
    public void getArgs() {
        assertEquals(TEST_ARGUMENTS, extractArguments(TEST_STRING));
    }
}
