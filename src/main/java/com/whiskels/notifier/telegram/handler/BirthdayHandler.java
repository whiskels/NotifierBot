package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.model.Employee;
import com.whiskels.notifier.model.User;
import com.whiskels.notifier.telegram.annotations.RequiredRoles;
import com.whiskels.notifier.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.whiskels.notifier.model.Role.*;
import static com.whiskels.notifier.util.TelegramUtil.*;

/**
 * Shows upcoming birthdays to employees
 * <p>
 * Available to: Employee, HR, Manager, Head, Admin
 */
@Component
@Slf4j
@BotCommand(command = "/BIRTHDAY", message = "Upcoming birthdays")
@Schedulable(roles = HR)
public class BirthdayHandler extends AbstractBaseHandler {
    @Value("${bot.server.hour.offset}")
    private int serverHourOffset;

    private final EmployeeService employeeService;

    public BirthdayHandler(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @RequiredRoles(roles = {EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /BIRTHDAY");
        LocalDate today = LocalDateTime.now().plusHours(serverHourOffset).toLocalDate();
        MessageBuilder builder = MessageBuilder.create(user)
                .line("*Birthdays*")
                .line("*Today (%s)*:", DATE_FORMATTER.format(today))
                .line(getBirthdayStringByPredicate(isBirthdayToday(today), false))
                .line()
                .line("*Upcoming week:*")
                .line(getBirthdayStringByPredicate(isBirthdayNextWeek(today), true));

        return List.of(builder.build());
    }

    private String getBirthdayStringByPredicate(Predicate<Employee> predicate, boolean withDate) {
        String birthdayInfo = "";
        try {
            if (withDate) {
                birthdayInfo = employeeStream(predicate)
                        .map(employee -> String.format("%s (%s)",
                                employee.getName(),
                                BIRTHDAY_FORMATTER.format(toLocalDate(employee.getBirthday()))))
                        .collect(Collectors.joining(", "));
            } else {
                birthdayInfo = employeeStream(predicate)
                        .map(Employee::getName)
                        .collect(Collectors.joining(", "));
            }
        } catch (Exception e) {
            log.error("Exception while creating message BIRTHDAY: {}", e.getMessage());
        }
        return birthdayInfo.isEmpty() ? "Nobody" : birthdayInfo;
    }

    private Stream<Employee> employeeStream(Predicate<Employee> predicate) {
        return employeeService.getEmployeeList().stream()
                .sorted(Comparator.comparing(Employee::getBirthday))
                .filter(predicate);
    }

    private Predicate<Employee> isBirthdayToday(LocalDate today) {
        return employee -> daysBetweenBirthdayAndToday(employee, today) == 0;
    }

    private Predicate<Employee> isBirthdayNextWeek(LocalDate today) {
        return employee -> {
            long daysUntilBirthday = daysBetweenBirthdayAndToday(employee, today);
            return daysUntilBirthday > 0 && daysUntilBirthday <= 7;
        };
    }

    private long daysBetweenBirthdayAndToday(Employee employee, LocalDate today) {
        return ChronoUnit.DAYS.between(
                today.atStartOfDay(),
                toLocalDate(employee.getBirthday()).withYear(today.getYear()).atStartOfDay());
    }
}
