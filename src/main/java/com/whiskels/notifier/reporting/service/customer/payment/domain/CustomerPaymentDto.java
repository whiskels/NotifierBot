package com.whiskels.notifier.reporting.service.customer.payment.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Comparator;

import static com.whiskels.notifier.utilities.formatters.StringFormatter.format;

@Value
@Builder
@Jacksonized
public class CustomerPaymentDto implements Comparable<CustomerPaymentDto> {
    private static final Comparator<CustomerPaymentDto> AMOUNT_COMPARATOR = Comparator.comparing(CustomerPaymentDto::getAmount)
            .thenComparing(CustomerPaymentDto::getContractor).reversed();

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
    public int compareTo(@Nonnull CustomerPaymentDto o) {
        return AMOUNT_COMPARATOR.compare(this, o);
    }

    public static CustomerPaymentDto from(FinancialOperation operation) {
        return CustomerPaymentDto.builder()
                .amount(operation.getAmount())
                .amountRub(operation.getAmountRub())
                .contractor(operation.getContractor())
                .currency(operation.getCurrency())
                .build();
    }
}
