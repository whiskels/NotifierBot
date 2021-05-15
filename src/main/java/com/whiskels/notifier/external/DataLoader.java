package com.whiskels.notifier.external;

import java.time.LocalDate;

public interface DataLoader<T> {
    void update();

    LocalDate lastUpdate();
}
