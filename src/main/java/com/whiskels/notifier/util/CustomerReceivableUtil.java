package com.whiskels.notifier.util;

import com.whiskels.notifier.model.CustomerReceivable;

import java.util.function.Predicate;

import static com.whiskels.notifier.model.CustomerReceivable.CATEGORY_REVENUE;

public final class CustomerReceivableUtil {
    private CustomerReceivableUtil() {
    }

    public static final Predicate<CustomerReceivable> IS_REVENUE =
            c -> c.getCategory().equalsIgnoreCase(CATEGORY_REVENUE);
}
