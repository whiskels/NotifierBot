package com.whiskels.notifier.common;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.whiskels.notifier.common.util.StreamUtil.alwaysTruePredicate;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamUtilTest {
    @Test
    void testFilterAndSort() {
        List<Integer> firstTestInput = List.of(4,3,2,1);
        List<Integer> firstTestExpected = List.of(1,2,3,4);
        assertEquals(firstTestExpected, filterAndSort(firstTestInput, alwaysTruePredicate()));

        List<Integer> expected = List.of(1,2,3);
        assertEquals(expected, filterAndSort(expected));
    }
}
