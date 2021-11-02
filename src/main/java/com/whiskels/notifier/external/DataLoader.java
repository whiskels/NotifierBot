package com.whiskels.notifier.external;

import java.time.LocalDate;
import java.util.Collection;

public interface DataLoader<T> {
    Collection<T> update();

    LocalDate lastUpdate();
}
