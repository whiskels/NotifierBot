package com.whiskels.notifier.external.debt.util;

import com.whiskels.notifier.external.debt.domain.Debt;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.AssertUtil.assertEqualsIgnoringCR;
import static com.whiskels.notifier.external.DebtTestData.debtOne;
import static com.whiskels.notifier.external.DebtTestData.debtTwo;
import static com.whiskels.notifier.external.MoexTestData.MOCK_RATE_EUR;
import static com.whiskels.notifier.external.MoexTestData.MOCK_RATE_USD;
import static com.whiskels.notifier.external.debt.util.DebtUtil.*;
import static junit.framework.TestCase.assertEquals;

class DebtUtilTest {
    @Test
    void testTotalDebtRoubleHigherThan() {
        List<Debt> debts = List.of(debtOne(), debtTwo());
        debts.forEach(debt -> {
            debt.setTotalDebt(calculateTotalDebt(debt));
            debt.setTotalDebtRouble(calculateTotalDebtRouble(debt, MOCK_RATE_USD, MOCK_RATE_EUR));
        });

        List<Debt> actual = debts.stream()
                .filter(totalDebtRoubleHigherThan(500))
                .collect(Collectors.toList());

        assertEquals(1, actual.size());
        assertEqualsIgnoringCR(debts.get(0), actual.get(0));
    }

    @Test
    void testCalculateTotalDebt() {
        assertEquals(250000.0, calculateTotalDebt(debtOne()));
    }

    @Test
    void testCalculateTotalDebtRouble() {
        Debt temp = debtOne();
        temp.setTotalDebt(250000.0);

        assertEquals(250000.0 * MOCK_RATE_USD,
                calculateTotalDebtRouble(temp, MOCK_RATE_USD, MOCK_RATE_EUR));
    }
}