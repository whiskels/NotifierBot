package com.whiskels.notifier.external;

import java.time.LocalDate;
import java.util.List;

public interface DataProvider<T> {
    List<T> get();

    LocalDate lastUpdate();
}
