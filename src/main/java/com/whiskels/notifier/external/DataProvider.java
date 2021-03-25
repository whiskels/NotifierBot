package com.whiskels.notifier.external;

import java.util.List;

public interface DataProvider<T> {
    List<T> get();
}
