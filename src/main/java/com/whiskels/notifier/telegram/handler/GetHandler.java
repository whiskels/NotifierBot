package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.CustomerDebt;
import com.whiskels.notifier.model.Role;
import com.whiskels.notifier.model.User;
import com.whiskels.notifier.service.CustomerDebtService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.RequiredRoles;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.whiskels.notifier.model.Role.*;

/**
 * Sends current customer overdue debts information
 * <p>
 * Available to: Manager, Head, Admin
 */
@Component
@Slf4j
@BotCommand(command = "/GET", message = "Get customer overdue debts")
@Schedulable(roles = {MANAGER, HEAD, ADMIN})
@Profile({"telegram", "telegram-test"})
public class GetHandler extends AbstractBaseHandler {
    private final CustomerDebtService customerDebtService;

    public GetHandler(CustomerDebtService customerDebtService) {
        this.customerDebtService = customerDebtService;
    }

    @Override
    @RequiredRoles(roles = {MANAGER, HEAD, ADMIN})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /GET");
        MessageBuilder builder = MessageBuilder.create(user)
                .line(customerDebtService.dailyMessage(isValid(user)));

        return List.of(builder.build());
    }

    /**
     * Checks if user is verified to get information about selected customer
     * <p>
     * true - if user is head or admin or is customer's account manager
     */
    private Predicate<CustomerDebt> isValid(User user) {
        return customerDebt -> {
            final Set<Role> roles = user.getRoles();
            return roles.contains(ADMIN)
                    || roles.contains(HEAD)
                    || roles.contains(MANAGER) && user.getName().equalsIgnoreCase(customerDebt.getAccountManager());
        };
    }
}
