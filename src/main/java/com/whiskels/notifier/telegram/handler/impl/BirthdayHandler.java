package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.employee.domain.Employee;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.isBirthdayNextWeekFrom;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.isBirthdayOn;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.builder.ReportBuilder.withHeader;
import static com.whiskels.notifier.telegram.domain.Role.*;
import static java.time.LocalDate.now;

/**
 * Shows upcoming birthdays to employees
 * <p>
 * Available to: Employee, HR, Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = "/BIRTHDAY", message = "Upcoming birthdays", requiredRoles = {EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
@Schedulable(roles = HR)
@ConditionalOnBean(value = Employee.class, parameterizedContainer = DataProvider.class)
public class BirthdayHandler extends AbstractBaseHandler {
    private static final String BIRTHDAY_REPORT_HEADER = "Birthdays";
    private static final String NO_DATA = "Nobody";
    private static final String UPCOMING_WEEK = "*Upcoming week:*";

    private final DataProvider<Employee> provider;
    private final Clock clock;

    public BirthdayHandler(AuthorizationService authorizationService,
                           ApplicationEventPublisher publisher,
                           DataProvider<Employee> provider,
                           Clock clock) {
        super(authorizationService, publisher);
        this.clock = clock;
        this.provider = provider;
    }

    @Override
    protected void handle(User user, String message) {
        final LocalDate today = now(clock);
        final List<Employee> filteredList = filterAndSort(provider.get());

        log.debug("Preparing /BIRTHDAY");
        publish(create(user)
                .line(withHeader(BIRTHDAY_REPORT_HEADER, today)
                        .setNoData(NO_DATA)
                        .list(filteredList, isBirthdayOn(today), COLLECTOR_COMMA_SEPARATED)
                        .line()
                        .line(UPCOMING_WEEK)
                        .list(filteredList, isBirthdayNextWeekFrom(today), COLLECTOR_COMMA_SEPARATED)
                        .build())
                .build());
    }
}
