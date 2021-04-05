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

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.isBirthdayNextWeekFrom;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.isBirthdayOn;
import static com.whiskels.notifier.telegram.Command.GET_BIRTHDAY;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.builder.ReportBuilder.withHeader;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Shows upcoming birthdays to employees
 * <p>
 * Available to: Employee, HR, Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = GET_BIRTHDAY, requiredRoles = {EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
@Schedulable(roles = HR)
@ConditionalOnBean(value = Employee.class, parameterizedContainer = DataProvider.class)
public class BirthdayHandler extends AbstractBaseHandler {
    private static final String BIRTHDAY_REPORT_HEADER = "Birthdays";
    private static final String NO_DATA = "Nobody";
    private static final String UPCOMING_WEEK = "*Upcoming week:*";

    private final DataProvider<Employee> provider;

    public BirthdayHandler(AuthorizationService authorizationService,
                           ApplicationEventPublisher publisher,
                           DataProvider<Employee> provider) {
        super(authorizationService, publisher);
        this.provider = provider;
    }

    @Override
    protected void handle(User user, String message) {
        final LocalDate reportDate = provider.lastUpdate();
        final List<Employee> filteredList = filterAndSort(provider.get());

        log.debug("Preparing /BIRTHDAY");
        publish(create(user)
                .line(withHeader(BIRTHDAY_REPORT_HEADER, reportDate)
                        .setNoData(NO_DATA)
                        .list(filteredList, isBirthdayOn(reportDate), COLLECTOR_COMMA_SEPARATED)
                        .line()
                        .line(UPCOMING_WEEK)
                        .list(filteredList, isBirthdayNextWeekFrom(reportDate), COLLECTOR_COMMA_SEPARATED)
                        .build())
                .build());
    }
}
