package com.whiskels.notifier.external.json.operation.util;

import com.whiskels.notifier.external.json.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.json.operation.dto.PaymentDto;
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
    public static final String DB_CATEGORY_PAYMENT = "Revenue";

    public static final Sort SORT_AMOUNT_RUB_DESC = by(desc("amountRub"));

    public static final Comparator<PaymentDto> AMOUNT_COMPARATOR = Comparator.comparing(PaymentDto::getAmount)
            .thenComparing(PaymentDto::getContractor).reversed();

    public static Predicate<FinancialOperation> newCrmId(List<Integer> ids) {
        return c -> !ids.contains(c.getCrmId());
    }

    public static Specification<FinancialOperation> loadDate(LocalDate localDate) {
        return (operation, cq, cb) -> cb.equal(operation.get("loadDate"), localDate);
    }

    public static Specification<FinancialOperation> category(String category) {
        return (operation, cq, cb) -> cb.equal(operation.get("category"), category);
    }
}
