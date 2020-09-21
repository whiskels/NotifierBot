package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.Customer;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.JSONReader;
import com.whiskels.telegrambot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.telegrambot.bot.command.Command.GET;

@Component
@Slf4j
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
    public List<PartialBotApiMethod<? extends Serializable>> operate(User user, Message message) {
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

    @Override
    public Command supportedCommand() {
        return GET;
    }

    private boolean isValid(User user, Customer customer) {
        return user.isHead() || user.isManager() && user.getName().equals(customer.getAccountManager());
    }
}
