package com.whiskels.notifier.common;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamUtilTest {
    private static final List<Integer> TEST_INPUT = List.of(1,2,3,4);
    private static final  Function<Integer, String> TEST_FUNC = integer -> format("a%db", integer);

    @Test
    void testFilterAndSort() {
        List<Integer> firstTestInput = List.of(4,3,2,1);
        List<Integer> firstTestExpected = List.of(1,2,3,4);
        assertEquals(firstTestExpected, filterAndSort(firstTestInput, alwaysTruePredicate()));
        assertEquals(firstTestExpected, filterAndSort(firstTestInput, List.of(alwaysTruePredicate())));

        List<Integer> expected = List.of(1,2,3);
        assertEquals(expected, filterAndSort(expected));

        List<Integer> expected2 = List.of(4,2);
        Predicate<Integer> testPredicate = integer -> integer % 2 == 0;
        assertEquals(expected2, filter(firstTestInput, testPredicate));
    }

    @Test
    void testMap() {
        assertEquals(List.of("a1b", "a2b", "a3b", "a4b"), map(TEST_INPUT, TEST_FUNC));
    }

    @Test
    void testCollectToString() {
        assertEquals("a1b, a2b, a3b, a4b", collectToString(TEST_INPUT, TEST_FUNC, COLLECTOR_COMMA_SEPARATED));
    }

    @Test
    void testCollectToBulletListString() {
        assertEquals(format("• a1b%n• a2b%n• a3b%n• a4b"), collectToBulletListString(TEST_INPUT, TEST_FUNC));
    }
}
