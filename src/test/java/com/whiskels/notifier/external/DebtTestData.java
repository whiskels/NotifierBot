package com.whiskels.notifier.external;

import com.whiskels.notifier.external.debt.Debt;
import lombok.experimental.UtilityClass;

import static org.assertj.core.api.Assertions.assertThat;

@UtilityClass
public class DebtTestData {
    public static final String DEBT_JSON = "json/external/debt.json";

    public static TestMatcher<Debt> RAW_DEBT_MATCHER =
            TestMatcher.usingAssertions(Debt.class,
                    (a, e) -> assertThat(a).usingRecursiveComparison()
                            .ignoringFields("totalDebt", "totalDebtRouble").ignoringAllOverriddenEquals().isEqualTo(e),
                    (a, e) -> {
                        throw new UnsupportedOperationException();
                    });

    public static Debt debtOne() {
        Debt debt = new Debt();
        debt.setContractor("Test contractor 1");
        debt.setFinanceSubject("Test subj 1");
        debt.setWayOfPayment("Test wop 1");
        debt.setAccountManager("Jason Bourne");
        debt.setCurrency("USD");
        debt.setDelay180(50000.0d);
        debt.setDelay90(50000.0d);
        debt.setDelay60(50000.0d);
        debt.setDelay30(50000.0d);
        debt.setDelay0(50000.0d);
        debt.setDebtorDelayCurrent("0");
        debt.setTotalDebt(0.0d);
        return debt;
    }

    public static Debt debtTwo() {
        Debt debt = new Debt();
        debt.setContractor("Test contractor 2");
        debt.setFinanceSubject("Test subj 2");
        debt.setWayOfPayment("Test wop 2");
        debt.setAccountManager("James Bond");
        debt.setCurrency("RUB");
        debt.setDelay180(0.0d);
        debt.setDelay90(0.0d);
        debt.setDelay60(0.0d);
        debt.setDelay30(0.0d);
        debt.setDelay0(0.0d);
        debt.setDebtorDelayCurrent("0");
        debt.setTotalDebt(0.0d);
        return debt;
    }


    public Debt copy(Debt original) {
        Debt debt = new Debt();
        debt.setContractor(original.getContractor());
        debt.setFinanceSubject(original.getFinanceSubject());
        debt.setWayOfPayment(original.getWayOfPayment());
        debt.setAccountManager(original.getAccountManager());
        debt.setCurrency(original.getCurrency());
        debt.setDelay180(original.getDelay180());
        debt.setDelay90(original.getDelay90());
        debt.setDelay60(original.getDelay60());
        debt.setDelay30(original.getDelay30());
        debt.setDelay0(original.getDelay0());
        debt.setDebtorDelayCurrent(original.getDebtorDelayCurrent());
        debt.setTotalDebt(original.getTotalDebt());
        return debt;
    }
}
