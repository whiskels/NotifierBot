package com.whiskels.notifier.utilities.collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamUtilTest {
    @Test
    @DisplayName("Should filter and sort Comparable")
    void filterAndSortComparable() {
        List<Integer> list = Arrays.asList(5, 3, 2, 4, 1);

        Predicate<Integer> greaterThanThree = num -> num > 3;
        List<Integer> result = StreamUtil.filterAndSort(list, greaterThanThree);

        assertEquals(Arrays.asList(4, 5), result);
    }

    @Test
    @DisplayName("Should filter and sort using comparator")
    void filterAndSortWithComparator() {
        List<String> list = Arrays.asList("apple", "banana", "cherry", "date");

        Predicate<String> lengthGreaterThanFour = str -> str.length() > 4;
        Comparator<String> reverseAlphabeticalOrder = Comparator.reverseOrder();
        List<String> result = StreamUtil.filterAndSort(list, reverseAlphabeticalOrder, lengthGreaterThanFour);

        assertEquals(Arrays.asList("cherry", "banana", "apple"), result);
    }

    @Test
    @DisplayName("Should map correctly")
    void map() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<String> result = StreamUtil.map(list, Object::toString);

        assertEquals(Arrays.asList("1", "2", "3", "4", "5"), result);
    }

    @Test
    @DisplayName("Should collect to bullet list")
    void collectToBulletListString() {
        List<String> list = Arrays.asList("apple", "banana", "cherry");
        String result = StreamUtil.collectToBulletListString(list, String::toUpperCase);

        assertEquals(String.format("• APPLE%n• BANANA%n• CHERRY"), result);
    }
}