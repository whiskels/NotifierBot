package com.whiskels.notifier.external.receivable.util;

import com.whiskels.notifier.external.receivable.domain.Receivable;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReceivableUtil {
    public static final String CATEGORY_REVENUE = "Revenue";

    public static final Comparator<ReceivableDto> AMOUNT_COMPARATOR = Comparator.comparing(ReceivableDto::getAmount)
            .thenComparing(ReceivableDto::getContractor).reversed();

    public static Predicate<Receivable> NEW_CRM_ID(List<Integer> ids) {
        return c -> !ids.contains(c.getCrmId());
    }
}
