package com.whiskels.notifier.external;

import java.util.List;

@FunctionalInterface
public interface Loader<T> {
    List<T> load();
}
