package com.whiskels.notifier.reporting.service.employee.convert.context;

import com.whiskels.notifier.reporting.service.employee.convert.ReportContext;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;

import java.time.LocalDate;
import java.util.function.BiPredicate;

import static com.whiskels.notifier.utilities.DateTimeUtil.isLater;
import static com.whiskels.notifier.utilities.DateTimeUtil.isSameMonth;
import static com.whiskels.notifier.utilities.DateTimeUtil.reportDate;

public class MonthMiddleReportContext extends ReportContext {
    private static final BiPredicate<Employee, LocalDate> BIRTHDAY_PREDICATE = (employee, date) -> {
        var birthday = employee.getBirthday();
        return isMiddleOfMonth(date) && isSameMonth(birthday, date) && isLater(birthday, date);
    };

    private static final BiPredicate<Employee, LocalDate> ANNIVERSARY_PREDICATE = (employee, date) -> {
        var appointmentDate = employee.getAppointmentDate();
        return isMiddleOfMonth(date) && isSameMonth(appointmentDate, date) && isLater(appointmentDate, date);
    };

    public MonthMiddleReportContext(String header) {
        super(_ignored -> header, date -> !isMiddleOfMonth(date), BIRTHDAY_PREDICATE, ANNIVERSARY_PREDICATE);
    }

    private static boolean isMiddleOfMonth(LocalDate date) {
        return date.getDayOfMonth() == 15;
    }
}
