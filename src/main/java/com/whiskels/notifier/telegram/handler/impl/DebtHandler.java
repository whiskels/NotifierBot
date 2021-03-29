package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_EMPTY_LINE;
import static com.whiskels.notifier.external.debt.util.DebtUtil.isValid;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.builder.ReportBuilder.withHeader;
import static com.whiskels.notifier.telegram.domain.Role.*;
import static java.time.LocalDate.now;

/**
 * Sends current customer overdue debts information
 * <p>
 * Available to: Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = "/GET", message = "Get customer overdue debts", requiredRoles = {MANAGER, HEAD, ADMIN})
@Schedulable(roles = {MANAGER, HEAD, ADMIN})
@ConditionalOnBean(value = Debt.class, parameterizedContainer = DataProvider.class)
public class DebtHandler extends AbstractBaseHandler {
    private static final String DEBT_REPORT_HEADER = "Overdue debts";

    private final DataProvider<Debt> provider;
    private final Clock clock;

    public DebtHandler(AuthorizationService authorizationService,
                       ApplicationEventPublisher publisher,
                       DataProvider<Debt> provider,
                       Clock clock) {
        super(authorizationService, publisher);
        this.clock = clock;
        this.provider = provider;
    }

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /GET");

        publish(create(user)
                .line(withHeader(DEBT_REPORT_HEADER, now(clock))
                        .list(provider.get(), isValid(user), COLLECTOR_EMPTY_LINE)
                        .build())
                .build());
    }
}
