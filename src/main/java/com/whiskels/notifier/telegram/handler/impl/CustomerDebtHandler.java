package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.debt.service.CustomerDebtService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.whiskels.notifier.external.debt.util.DebtUtil.isValid;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Sends current customer overdue debts information
 * <p>
 * Available to: Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = "/GET", message = "Get customer overdue debts", requiredRoles = {MANAGER, HEAD, ADMIN})
@Schedulable(roles = {MANAGER, HEAD, ADMIN})
@ConditionalOnBean(CustomerDebtService.class)
public class CustomerDebtHandler extends AbstractBaseHandler {
    private final CustomerDebtService customerDebtService;

    public CustomerDebtHandler(CustomerDebtService customerDebtService) {
        this.customerDebtService = customerDebtService;
    }

    @Override
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /GET");
        MessageBuilder builder = MessageBuilder.create(user)
                .line(customerDebtService.dailyReport(isValid(user)));

        return List.of(builder.build());
    }
}
