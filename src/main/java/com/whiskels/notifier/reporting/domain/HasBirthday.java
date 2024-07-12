package com.whiskels.notifier.reporting.domain;

import java.time.LocalDate;
import java.util.Comparator;

public interface HasBirthday {
    static <T extends HasBirthday> Comparator<T> comparator() {
        return Comparator.comparing(T::birthday, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(T::name, Comparator.nullsLast(Comparator.naturalOrder()));
    }


    String name();

    LocalDate birthday();
}
