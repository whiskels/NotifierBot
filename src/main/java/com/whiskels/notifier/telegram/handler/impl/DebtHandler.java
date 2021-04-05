package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Set;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_EMPTY_LINE;
import static com.whiskels.notifier.telegram.Command.GET_DEBT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.builder.ReportBuilder.withHeader;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Sends current customer overdue debts information
 * <p>
 * Available to: Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = GET_DEBT, requiredRoles = {MANAGER, HEAD, ADMIN})
@Schedulable(roles = {MANAGER, HEAD, ADMIN})
@ConditionalOnBean(value = Debt.class, parameterizedContainer = DataProvider.class)
public class DebtHandler extends AbstractBaseHandler {
    private static final String DEBT_REPORT_HEADER = "Overdue debts";

    private final DataProvider<Debt> provider;

    public DebtHandler(AuthorizationService authorizationService,
                       ApplicationEventPublisher publisher,
                       DataProvider<Debt> provider) {
        super(authorizationService, publisher);
        this.provider = provider;
    }

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /GET_DEBT");

        publish(create(user)
                .line(withHeader(DEBT_REPORT_HEADER, provider.lastUpdate())
                        .list(provider.get(), isValid(user), COLLECTOR_EMPTY_LINE)
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
