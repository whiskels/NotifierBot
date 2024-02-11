package com.whiskels.notifier.reporting.domain;

import java.time.LocalDate;
import java.util.Comparator;

public interface HasBirthday {
    static <T extends HasBirthday> Comparator<T> comparator() {
        return Comparator.comparing(T::birthday).thenComparing(T::name);
    }


    String name();

    LocalDate birthday();
}
