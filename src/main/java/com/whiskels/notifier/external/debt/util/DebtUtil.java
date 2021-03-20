package com.whiskels.notifier.external.debt.util;

import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

import static com.whiskels.notifier.telegram.domain.Role.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebtUtil {
    public static final Comparator<Debt> TOTAL_DEBT_COMPARATOR = Comparator.comparing(Debt::getTotalDebtRouble)
            .thenComparing(Debt::getContractor).reversed();

    /**
     * Checks if user is verified to get information about selected customer
     * <p>
     * true - if user is head or admin or is customer's account manager
     */
    public static Predicate<Debt> isValid(User user) {
        return debt -> {
            final Set<Role> roles = user.getRoles();
            return roles.contains(ADMIN)
                    || roles.contains(HEAD)
                    || roles.contains(MANAGER) && user.getName().equalsIgnoreCase(debt.getAccountManager());
        };
    }

    public static Predicate<Debt> totalDebtRoubleHigherThan(int amount) {
        return debt -> debt.getTotalDebtRouble() > amount;
    }

    public static double calculateTotalDebt(Debt debt) {
        return debt.getDelay0() + debt.getDelay30() + debt.getDelay60()
                + debt.getDelay90() + debt.getDelay180();
    }

    /*
     * Calculates overall customer debt with delay more than 0 days
     */
    public static double calculateTotalDebtRouble(Debt debt, double usdRate, double eurRate) {
        final double totalDebt = debt.getTotalDebt();
        final String currency = debt.getCurrency();
        if (!StringUtils.isEmpty(currency)) {
            if (currency.equalsIgnoreCase("USD")) {
                return totalDebt * usdRate;
            } else if (currency.equalsIgnoreCase("EUR")) {
                return totalDebt * eurRate;
            }
        }
        return totalDebt;
    }
}
