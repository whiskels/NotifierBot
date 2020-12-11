package com.whiskels.notifier.util;

import com.whiskels.notifier.model.CustomerDebt;
import com.whiskels.notifier.model.Role;
import com.whiskels.notifier.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

import static com.whiskels.notifier.model.Role.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerDebtUtil {
    public static final Comparator<CustomerDebt> TOTAL_DEBT_COMPARATOR =  Comparator.comparing(CustomerDebt::getTotalDebtRouble)
                .thenComparing(CustomerDebt::getContractor).reversed();

    /**
     * Checks if user is verified to get information about selected customer
     * <p>
     * true - if user is head or admin or is customer's account manager
     */
    public static Predicate<CustomerDebt> isValid(User user) {
        return customerDebt -> {
            final Set<Role> roles = user.getRoles();
            return roles.contains(ADMIN)
                    || roles.contains(HEAD)
                    || roles.contains(MANAGER) && user.getName().equalsIgnoreCase(customerDebt.getAccountManager());
        };
    }

    public static Predicate<CustomerDebt> totalDebtRoubleHigherThan(int amount) {
        return customerDebt -> customerDebt.getTotalDebtRouble() > amount;
    }
}
