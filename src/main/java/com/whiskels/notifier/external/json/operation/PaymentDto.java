package com.whiskels.notifier.external.json.operation;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.whiskels.notifier.common.util.FormatUtil.format;
import static com.whiskels.notifier.external.json.operation.FinOperationUtil.AMOUNT_COMPARATOR;

@Value
@Builder
public class PaymentDto implements Comparable<PaymentDto> {
    String currency;
    BigDecimal amount;
    String contractor;
    BigDecimal amountRub;

    @Override
    public String toString() {
        return String.format("%s — %s %s",
                contractor, format(amount), currency);
    }

    @Override
    public int compareTo(@NotNull PaymentDto o) {
        return AMOUNT_COMPARATOR.compare(this, o);
    }

    public static PaymentDto from(FinancialOperation operation) {
        return PaymentDto.builder()
                .amount(operation.getAmount())
                .amountRub(operation.getAmountRub())
                .contractor(operation.getContractor())
                .currency(operation.getCurrency())
                .build();
    }
}
