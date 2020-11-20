package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.Customer;
import com.whiskels.notifier.model.Role;
import com.whiskels.notifier.model.User;
import com.whiskels.notifier.service.CustomerService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.RequiredRoles;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
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
public class GetHandler extends AbstractBaseHandler {
    private final CustomerService customerService;

    public GetHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    @RequiredRoles(roles = {MANAGER, HEAD, ADMIN})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /GET");
        MessageBuilder builder = MessageBuilder.create(user)
                .line(customerService.createCustomerDebtMessage(isValid(user)));

        return List.of(builder.build());
    }

    /**
     * Checks if user is verified to get information about selected customer
     * <p>
     * true - if user is head or admin or is customer's account manager
     */
    private Predicate<Customer> isValid(User user) {
        return customer -> {
            final Set<Role> roles = user.getRoles();
            return roles.contains(ADMIN)
                    || roles.contains(HEAD)
                    || roles.contains(MANAGER) && user.getName().equalsIgnoreCase(customer.getAccountManager());
        };
    }
}
