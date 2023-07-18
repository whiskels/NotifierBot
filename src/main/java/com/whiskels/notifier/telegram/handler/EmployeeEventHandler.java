package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.common.util.Util;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.employee.EmployeeDto;
import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.ScheduledCommandHandler;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.security.Secured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.whiskels.notifier.common.util.DateTimeUtil.isSameMonth;
import static com.whiskels.notifier.common.util.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.EMPLOYEE_ANNIVERSARY_COMPARATOR;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.EMPLOYEE_BIRTHDAY_COMPARATOR;
import static com.whiskels.notifier.telegram.Command.GET_EVENT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static com.whiskels.notifier.telegram.domain.Role.EMPLOYEE;
import static com.whiskels.notifier.telegram.domain.Role.HEAD;
import static com.whiskels.notifier.telegram.domain.Role.HR;
import static com.whiskels.notifier.telegram.domain.Role.MANAGER;

@Service
@ConditionalOnBean(value = EmployeeDto.class, parameterizedContainer = ReportSupplier.class)
class EmployeeEventHandler implements ScheduledCommandHandler {
    private final String header;
    private final String noData;
    private final String upcomingMonth;
    private final String anniversary;
    private final ReportSupplier<EmployeeDto> provider;

    public EmployeeEventHandler(@Value("${telegram.report.employee.birthday.header:Employee events on}") String header,
                                @Value("${telegram.report.employee.birthday.no-data:Nobody}") String noData,
                                @Value("${telegram.report.employee.birthday:*Birthdays:*}") String upcomingMonth,
                                @Value("${telegram.report.employee.anniversary:*Work anniversaries:*}") String anniversary,
                                ReportSupplier<EmployeeDto> provider) {
        this.header = header;
        this.noData = noData;
        this.upcomingMonth = upcomingMonth;
        this.anniversary = anniversary;
        this.provider = provider;
    }

    @Override
    @Secured({EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
    public SendMessage handle(User user, String message) {
        var data = provider.get();
        return builder(user)
                .line(ReportBuilder.builder(header + reportDate(data.getReportDate()))
                        .setNoData(noData)
                        .setActiveCollector(COLLECTOR_COMMA_SEPARATED)
                        .line(upcomingMonth)
                        .list(filteredBy(EmployeeDto::getBirthday, EMPLOYEE_BIRTHDAY_COMPARATOR), EmployeeDto::toBirthdayString)
                        .line(anniversary)
                        .list(filteredBy(EmployeeDto::getAppointmentDate, EMPLOYEE_ANNIVERSARY_COMPARATOR), EmployeeDto::toWorkAnniversaryString)
                        .build())
                .build();
    }

    @Override
    public Command getCommand() {
        return GET_EVENT;
    }

    private List<EmployeeDto> filteredBy(Function<EmployeeDto, LocalDate> dateFunc, Comparator<EmployeeDto> comparator) {
        var data = provider.get();
        return filterAndSort(data.getContent(),
                comparator,
                Util.notNull(dateFunc),
                isSameMonth(dateFunc, data.getReportDate()));
    }
}
