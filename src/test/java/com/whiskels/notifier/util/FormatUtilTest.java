package com.whiskels.notifier.util;

import org.junit.Test;

import static com.whiskels.notifier.util.FormatUtil.extractArguments;
import static com.whiskels.notifier.util.FormatUtil.extractCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormatUtilTest {
    private static final String TEST_COMMAND = "/GET";
    private static final String TEST_ARGUMENTS = "some arguments";
    private static final String TEST_STRING = TEST_COMMAND + " " + TEST_ARGUMENTS;

    @Test
    public void getCommand() {
        assertEquals(TEST_COMMAND, extractCommand(TEST_STRING));
    }

    @Test
    public void getArgs() {
        assertEquals(TEST_ARGUMENTS, extractArguments(TEST_STRING));
    }
}
