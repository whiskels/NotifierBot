package com.whiskels.notifier.external;

import java.time.LocalDate;
import java.util.List;

public interface Supplier<T> {
    List<T> getData();

    LocalDate lastUpdate();

    void update();
}
