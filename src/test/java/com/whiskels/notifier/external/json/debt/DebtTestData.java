package com.whiskels.notifier.external.json.debt;

import com.whiskels.notifier.external.TestMatcher;
import lombok.experimental.UtilityClass;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

@UtilityClass
public class DebtTestData {
    public static final String DEBT_JSON = "json/external/debt.json";

    public static TestMatcher<Debt> RAW_DEBT_MATCHER =
            TestMatcher.usingAssertions(Debt.class,
                    (a, e) -> assertThat(a).usingRecursiveComparison()
                            .ignoringFields("total", "totalRouble").ignoringAllOverriddenEquals().isEqualTo(e),
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
        debt.setDelay180(valueOf(50000));
        debt.setDelay90(valueOf(50000));
        debt.setDelay60(valueOf(50000));
        debt.setDelay30(valueOf(50000));
        debt.setDelay0(valueOf(50000));
        debt.setDelayCurrent("0");
        debt.setTotal(null);
        return debt;
    }

    public static Debt debtTwo() {
        Debt debt = new Debt();
        debt.setContractor("Test contractor 2");
        debt.setFinanceSubject("Test subj 2");
        debt.setWayOfPayment("Test wop 2");
        debt.setAccountManager("James Bond");
        debt.setCurrency("RUB");
        debt.setDelay180(valueOf(0));
        debt.setDelay90(valueOf(0));
        debt.setDelay60(valueOf(0));
        debt.setDelay30(valueOf(0));
        debt.setDelay0(valueOf(0));
        debt.setDelayCurrent("0");
        debt.setTotal(null);
        return debt;
    }

    public static DebtDto debtDtoOne() {
        var debt = debtOne();
        debt.calculateTotal();
        return DebtDto.from(debt);
    }

    public static DebtDto debtDtoTwo() {
        var debt = debtTwo();
        debt.calculateTotal();
        return DebtDto.from(debt);
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
        debt.setDelayCurrent(original.getDelayCurrent());
        debt.setTotal(original.getTotal());
        return debt;
    }
}
