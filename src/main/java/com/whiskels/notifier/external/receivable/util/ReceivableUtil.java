package com.whiskels.notifier.external.receivable.util;

import com.whiskels.notifier.external.receivable.domain.Receivable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReceivableUtil {
    public static final String CATEGORY_REVENUE = "Revenue";

    public static final Predicate<Receivable> IS_REVENUE =
            c -> c.getCategory().equalsIgnoreCase(CATEGORY_REVENUE);

    public static final Comparator<Receivable> AMOUNT_COMPARATOR = Comparator.comparing(Receivable::getAmountRub)
            .thenComparing(Receivable::getContractor);
}
