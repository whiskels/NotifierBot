package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.User;
import com.whiskels.notifier.service.EmployeeService;
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

import static com.whiskels.notifier.model.Role.*;

/**
 * Shows upcoming birthdays to employees
 * <p>
 * Available to: Employee, HR, Manager, Head, Admin
 */
@Component
@Slf4j
@BotCommand(command = "/BIRTHDAY", message = "Upcoming birthdays")
@Schedulable(roles = HR)
@Profile({"telegram", "telegram-test"})
public class BirthdayHandler extends AbstractBaseHandler {
    private final EmployeeService employeeService;

    public BirthdayHandler(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @RequiredRoles(roles = {EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /BIRTHDAY");

        MessageBuilder builder = MessageBuilder.create(user)
                .line(employeeService.dailyMessage());

        return List.of(builder.build());
    }
}
