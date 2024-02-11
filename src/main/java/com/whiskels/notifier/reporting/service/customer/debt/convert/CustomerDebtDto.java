package com.whiskels.notifier.reporting.service.customer.debt.convert;

import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import com.whiskels.notifier.utilities.formatters.StringFormatter;
import lombok.Builder;

import java.math.BigDecimal;

import static com.whiskels.notifier.utilities.Util.defaultIfNull;
import static java.math.BigDecimal.ZERO;

@Builder
record CustomerDebtDto(String contractor, String financeSubject, String wayOfPayment,
                       String accountManager, String currency, String comment,
                       BigDecimal total) {
    public static CustomerDebtDto from(CustomerDebt customerDebt) {
        return CustomerDebtDto.builder()
                .contractor(customerDebt.getContractor())
                .accountManager(customerDebt.getAccountManager())
                .financeSubject(customerDebt.getFinanceSubject())
                .wayOfPayment(customerDebt.getWayOfPayment())
                .financeSubject(customerDebt.getFinanceSubject())
                .currency(customerDebt.getCurrency())
                .comment(customerDebt.getComment())
                .total(customerDebt.getTotal())
                .build();
    }

    @Override
    public String toString() {
        return String.format("*%s*%n   %s%n   %s%n   %s%n   *%s %s*%n%s"
                , defaultIfNull(contractor, "Contractor not defined")
                , defaultIfNull(financeSubject, "Finance subject not defined")
                , defaultIfNull(wayOfPayment, "Way of payment not defined")
                , defaultIfNull(accountManager, "Account manager not defined")
                , StringFormatter.format(defaultIfNull(total, ZERO))
                , defaultIfNull(currency, "Currency not defined")
                , defaultIfNull(comment, "No comment")
        );
    }
}
