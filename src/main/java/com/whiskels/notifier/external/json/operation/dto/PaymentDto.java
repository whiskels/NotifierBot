package com.whiskels.notifier.external.json.operation.dto;

import com.whiskels.notifier.external.json.operation.domain.FinancialOperation;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import static com.whiskels.notifier.common.util.FormatUtil.formatDouble;
import static com.whiskels.notifier.external.json.operation.util.FinOperationUtil.AMOUNT_COMPARATOR;

@Value
@Builder
public class PaymentDto implements Comparable<PaymentDto> {
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
    public int compareTo(@NotNull PaymentDto o) {
        return AMOUNT_COMPARATOR.compare(this, o);
    }

    public static PaymentDto fromEntity(FinancialOperation operation) {
        return PaymentDto.builder()
                .amount(operation.getAmount())
                .amountRub(operation.getAmountRub())
                .contractor(operation.getContractor())
                .currency(operation.getCurrency())
                .build();
    }
}
