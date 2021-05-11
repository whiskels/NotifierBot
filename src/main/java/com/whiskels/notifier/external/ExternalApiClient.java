package com.whiskels.notifier.external;

import java.time.LocalDate;

public interface ExternalApiClient<T> {
    void update();

    LocalDate lastUpdate();
}
