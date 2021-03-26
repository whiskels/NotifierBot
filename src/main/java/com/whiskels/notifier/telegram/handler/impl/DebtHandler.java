package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import static com.whiskels.notifier.external.debt.util.DebtUtil.isValid;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Sends current customer overdue debts information
 * <p>
 * Available to: Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = "/GET", message = "Get customer overdue debts", requiredRoles = {MANAGER, HEAD, ADMIN})
@Schedulable(roles = {MANAGER, HEAD, ADMIN})
@ConditionalOnBean(value = Debt.class, parameterizedContainer = DailyReporter.class)
@RequiredArgsConstructor
public class DebtHandler extends AbstractBaseHandler {
    private final DailyReporter<Debt> reporter;

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /GET");

        publish(create(user)
                .line(reporter.dailyReport(isValid(user)))
                .build());
    }
}
