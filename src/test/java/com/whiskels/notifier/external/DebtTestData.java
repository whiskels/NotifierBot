package com.whiskels.notifier.external;

import com.whiskels.notifier.external.debt.domain.Debt;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DebtTestData {
    public static final String DEBT_JSON = "json/external/debt.json";

    public static Debt debtOne() {
        Debt debt = new Debt();
        debt.setContractor("Test contractor 1");
        debt.setFinanceSubject("Test subj 1");
        debt.setWayOfPayment("Test wop 1");
        debt.setAccountManager("Jason Bourne");
        debt.setCurrency("RUB");
        debt.setDelay180(5000000.0d);
        debt.setDelay90(5000000.0d);
        debt.setDelay60(5000000.0d);
        debt.setDelay30(5000000.0d);
        debt.setDelay0(5000000.0d);
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
}
