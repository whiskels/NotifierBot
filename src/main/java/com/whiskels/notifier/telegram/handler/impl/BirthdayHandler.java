package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.employee.domain.Employee;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Shows upcoming birthdays to employees
 * <p>
 * Available to: Employee, HR, Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = "/BIRTHDAY", message = "Upcoming birthdays", requiredRoles = {EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
@Schedulable(roles = HR)
@ConditionalOnBean(value = Employee.class, parameterizedContainer = DailyReporter.class)
@RequiredArgsConstructor
public class BirthdayHandler extends AbstractBaseHandler {
    private final DailyReporter<Employee> reporter;

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /BIRTHDAY");
        publish(create(user)
                .line(reporter.dailyReport())
                .build());
    }
}
