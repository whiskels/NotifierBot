package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.employee.service.EmployeeService;
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

import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Shows upcoming birthdays to employees
 * <p>
 * Available to: Employee, HR, Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = "/BIRTHDAY", message = "Upcoming birthdays", requiredRoles = {EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
@Schedulable(roles = HR)
@ConditionalOnBean(EmployeeService.class)
public class BirthdayHandler extends AbstractBaseHandler {
    private final EmployeeService employeeService;

    public BirthdayHandler(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /BIRTHDAY");

        MessageBuilder builder = MessageBuilder.create(user)
                .line(employeeService.dailyReport());

        return List.of(builder.build());
    }
}
