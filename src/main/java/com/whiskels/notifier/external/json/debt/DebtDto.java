package com.whiskels.notifier.external.json.debt;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

import static com.whiskels.notifier.common.util.FormatUtil.format;
import static com.whiskels.notifier.common.util.Util.defaultIfNull;
import static java.math.BigDecimal.ZERO;

@Builder
@Value
public class DebtDto {
    String contractor;
    String financeSubject;
    String wayOfPayment;
    String accountManager;
    String currency;
    String comment;
    BigDecimal total;

    public static DebtDto from(Debt debt) {
        return DebtDto.builder()
                .contractor(debt.getContractor())
                .accountManager(debt.getAccountManager())
                .financeSubject(debt.getFinanceSubject())
                .wayOfPayment(debt.getWayOfPayment())
                .financeSubject(debt.getFinanceSubject())
                .currency(debt.getCurrency())
                .comment(debt.getComment())
                .total(debt.getTotal())
                .build();
    }

    @Override
    public String toString() {
        return String.format("*%s*%n   %s%n   %s%n   %s%n   *%s %s*%n%s"
                , defaultIfNull(contractor, "Contractor not defined")
                , defaultIfNull(financeSubject, "Finance subject not defined")
                , defaultIfNull(wayOfPayment, "Way of payment not defined")
                , defaultIfNull(accountManager, "Account manager not defined")
                , format(defaultIfNull(total, ZERO))
                , defaultIfNull(currency, "Currency not defined")
                , defaultIfNull(comment,"No comment")
        );
    }
}
