package com.whiskels.notifier.external;

import java.util.List;

public interface Loader<T> {
    List<T> load();
}
