package com.whiskels.notifier.external.operation.util;

import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.operation.dto.FinancialOperationDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.domain.Sort.by;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FinOperationUtil {
    public static final String CATEGORY_PAYMENT = "Payment";
    public static final String DB_CATEGORY_PAYMENT = "Revenue";

    public static final Comparator<FinancialOperationDto> AMOUNT_COMPARATOR = Comparator.comparing(FinancialOperationDto::getAmount)
            .thenComparing(FinancialOperationDto::getContractor).reversed();

    public static Predicate<FinancialOperation> NEW_CRM_ID(List<Integer> ids) {
        return c -> !ids.contains(c.getCrmId());
    }

    public static Specification<FinancialOperation> loadDate(LocalDate localDate) {
        return (operation, cq, cb) -> cb.equal(operation.get("loadDate"), localDate);
    }

    public static Specification<FinancialOperation> category(String category) {
        return (operation, cq, cb) -> cb.equal(operation.get("category"), category);
    }

    public static final Sort SORT_AMOUNT_RUB_DESC = by(desc("amountRub"));

    public static double calculateRoubleAmount(FinancialOperation financialOperation, double usdRate, double eurRate) {
        final double amount = financialOperation.getAmount();
        final String currency = financialOperation.getCurrency();
            if (currency.equalsIgnoreCase("USD")) {
                return amount * usdRate;
            } else if (currency.equalsIgnoreCase("EUR")) {
                return amount * eurRate;
            }
        return amount;
    }
}
