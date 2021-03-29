package com.whiskels.notifier.external.receivable.dto;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import static com.whiskels.notifier.common.FormatUtil.formatDouble;
import static com.whiskels.notifier.external.receivable.util.ReceivableUtil.AMOUNT_COMPARATOR;

@Value
public class ReceivableDto implements Comparable<ReceivableDto> {
    String currency;
    double amount;
    String contractor;
    double amountRub;

    @Override
    public String toString() {
        return String.format("%s — %s %s",
                contractor, formatDouble(amount), currency);
    }

    @Override
    public int compareTo(@NotNull ReceivableDto o) {
        return AMOUNT_COMPARATOR.compare(this, o);
    }
}
