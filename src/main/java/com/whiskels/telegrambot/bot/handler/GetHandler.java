package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.BotCommand;
import com.whiskels.telegrambot.bot.builder.MessageBuilder;
import com.whiskels.telegrambot.model.Customer;
import com.whiskels.telegrambot.model.Role;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.RequiredRoles;
import com.whiskels.telegrambot.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.whiskels.telegrambot.model.Role.*;
import static com.whiskels.telegrambot.util.TelegramUtil.DATE_YEAR_FORMATTER;
import static com.whiskels.telegrambot.util.TelegramUtil.EMPTY_LINE;

/**
 * Sends current customer overdue debts information
 * <p>
 * Available to: Manager, Head, Admin
 */
@Component
@Slf4j
@BotCommand(command = "/GET", message = "Get customer overdue debts")
public class GetHandler extends AbstractBaseHandler {
    @Value("${bot.server.hour.offset}")
    private int serverHourOffset;

    private final CustomerService customerService;

    public GetHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    @RequiredRoles(roles = {MANAGER, HEAD, ADMIN})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /GET");
        MessageBuilder builder = MessageBuilder.create(user)
                .line("Overdue debts on %s",
                        DATE_YEAR_FORMATTER.format(LocalDateTime.now().plusHours(serverHourOffset)))
                .line();
        String customerInfo = "";
        try {
            customerInfo = customerService.getCustomerList().stream()
                    .filter(customer -> isValid(user, customer))
                    .map(Customer::toString)
                    .collect(Collectors.joining(String.format(
                            "%n%s%n", EMPTY_LINE)));

        } catch (Exception e) {
            log.error("Exception while creating message GET: {}", e.getMessage());
        }

        builder.line(customerInfo.isEmpty() ? "No overdue debts" : customerInfo);

        return List.of(builder.build());
    }

    /**
     * Checks if user is verified to get information about selected customer
     * <p>
     * true - if user is head or admin or is customer's account manager
     */
    private boolean isValid(User user, Customer customer) {
        final Set<Role> roles = user.getRoles();
        return roles.contains(ADMIN)
                || roles.contains(HEAD)
                || roles.contains(MANAGER) && user.getName().equalsIgnoreCase(customer.getAccountManager());
    }
}
