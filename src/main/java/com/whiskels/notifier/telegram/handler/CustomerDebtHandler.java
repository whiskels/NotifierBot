package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.User;
import com.whiskels.notifier.service.CustomerDebtService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.whiskels.notifier.model.Role.*;
import static com.whiskels.notifier.util.CustomerDebtUtil.isValid;

/**
 * Sends current customer overdue debts information
 * <p>
 * Available to: Manager, Head, Admin
 */
@Component
@Slf4j
@BotCommand(command = "/GET", message = "Get customer overdue debts", requiredRoles = {MANAGER, HEAD, ADMIN})
@Schedulable(roles = {MANAGER, HEAD, ADMIN})
@Profile("telegram-common")
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
