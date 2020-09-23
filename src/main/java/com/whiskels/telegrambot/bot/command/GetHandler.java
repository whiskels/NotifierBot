package com.whiskels.telegrambot.bot.command;

import com.whiskels.telegrambot.model.Customer;
import com.whiskels.telegrambot.model.Role;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.RequiredRoles;
import com.whiskels.telegrambot.service.JSONReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.whiskels.telegrambot.model.Role.*;
import static com.whiskels.telegrambot.util.TelegramUtils.createMessageTemplate;

@Component
@Slf4j
@BotCommand(command = "/GET")
public class GetHandler extends AbstractBaseHandler {
    private final JSONReader jsonReader;

    private List<Customer> customerList;

    public GetHandler(JSONReader jsonReader) {
        this.jsonReader = jsonReader;
    }

    @PostConstruct
    private void initCustomerList() {
        updateCustomers();
    }

    @Override
    @RequiredRoles(roles = {HEAD, MANAGER, ADMIN})
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage sendMessage = createMessageTemplate(user);

        StringBuilder text = new StringBuilder();
        text.append(String.format("Overdue debts on %s%n%n", DATE_TIME_FORMATTER.format(LocalDateTime.now().plusHours(3))));
        try {
            StringBuilder list = new StringBuilder();

            list.append(customerList.stream()
                    .filter(customer -> isValid(user, customer))
                    .map(Customer::toString)
                    .collect(Collectors.joining(String.format(
                            "%s%s%s", END_LINE, EMPTY_LINE, END_LINE))));

            if (list.length() == 0) {
                list.append("No overdue debts");
            }
            text.append(list.toString());

        } catch (Exception e) {
            log.error("Exception while creating message GET: {}", e.getMessage());
        }
        sendMessage.setText(text.toString());

        return Collections.singletonList(sendMessage);
    }

    public void updateCustomers() {
        try {
            jsonReader.update();
            customerList = jsonReader.getCustomerList();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean isValid(User user, Customer customer) {
        final Set<Role> roles = user.getRoles();
        return roles.contains(ADMIN)
                || roles.contains(HEAD)
                || roles.contains(MANAGER) && user.getName().equalsIgnoreCase(customer.getAccountManager());
    }
}
