package com.whiskels.notifier.reporting.service.employee.convert.context;

import com.whiskels.notifier.reporting.service.employee.convert.ReportContext;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.BiPredicate;

import static com.whiskels.notifier.utilities.DateTimeUtil.isSameDay;
import static com.whiskels.notifier.utilities.DateTimeUtil.isSameMonth;
import static com.whiskels.notifier.utilities.DateTimeUtil.reportDate;

public class MonthStartReportContext extends ReportContext {
    private static final BiPredicate<Employee, LocalDate> BIRTHDAY_PREDICATE = (employee, date) -> {
        var birthday = employee.getBirthday();
        return isStartOfMonth(date) && isSameMonth(birthday, date);
    };

    private static final BiPredicate<Employee, LocalDate> ANNIVERSARY_PREDICATE = (employee, date) -> {
        var appointmentDate = employee.getAppointmentDate();
        return isStartOfMonth(date) && isSameMonth(appointmentDate, date);
    };

    public MonthStartReportContext(String header) {
        super(_ignored -> header, date -> !isStartOfMonth(date), BIRTHDAY_PREDICATE, ANNIVERSARY_PREDICATE);
    }

    private static boolean isStartOfMonth(LocalDate date) {
        return date.getDayOfMonth() == 1;
    }
}
