package com.whiskels.notifier;

import lombok.experimental.UtilityClass;

import static junit.framework.TestCase.assertEquals;

@UtilityClass
public class AssertUtil {
    public static <T> void assertEqualsIgnoringCR(T o1, T o2) {
        assertEquals(o1.toString().replace("\r",""), o2.toString().replace("\r",""));
    }
}
