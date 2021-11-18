package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.debt.Debt;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.annotation.Schedulable;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import java.util.Set;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_EMPTY_LINE;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.telegram.Command.GET_DEBT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Sends current customer overdue debts information
 * <p>
 * Available to: Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = GET_DEBT, requiredRoles = {MANAGER, HEAD, ADMIN})
@Schedulable(roles = {MANAGER, HEAD, ADMIN})
@ConditionalOnBean(value = Debt.class, parameterizedContainer = Supplier.class)
@RequiredArgsConstructor
public class DebtHandler extends AbstractBaseHandler {
    @Value("${telegram.report.customer.debt.header:Overdue debts report on}")
    private String header;

    private final Supplier<Debt> provider;

    @Override
    protected void handle(User user, String message) {
        publish(builder(user)
                .line(ReportBuilder.builder(header + reportDate(provider.lastUpdate()))
                        .setActiveCollector(COLLECTOR_EMPTY_LINE)
                        .list(filterAndSort(provider.getData(), isValid(user)))
                        .build())
                .build());
    }

    private static Predicate<Debt> isValid(User user) {
        return debt -> {
            final Set<Role> roles = user.getRoles();
            return roles.contains(ADMIN)
                    || roles.contains(HEAD)
                    || roles.contains(MANAGER) && user.getName().equalsIgnoreCase(debt.getAccountManager());
        };
    }
}
