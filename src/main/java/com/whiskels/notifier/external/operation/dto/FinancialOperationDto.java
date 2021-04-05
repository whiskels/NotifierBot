package com.whiskels.notifier.external.operation.dto;

import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import static com.whiskels.notifier.common.FormatUtil.formatDouble;
import static com.whiskels.notifier.external.operation.util.FinOperationUtil.AMOUNT_COMPARATOR;

@Value
@Builder
public class FinancialOperationDto implements Comparable<FinancialOperationDto> {
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
    public int compareTo(@NotNull FinancialOperationDto o) {
        return AMOUNT_COMPARATOR.compare(this, o);
    }

    public static FinancialOperationDto fromEntity(FinancialOperation operation) {
        return FinancialOperationDto.builder()
                .amount(operation.getAmount())
                .amountRub(operation.getAmountRub())
                .contractor(operation.getContractor())
                .currency(operation.getCurrency())
                .build();
    }
}
