package com.whiskels.notifier.reporting.service.employee.convert.context;

import com.whiskels.notifier.reporting.service.employee.convert.ReportContext;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.BiPredicate;

import static com.whiskels.notifier.utilities.DateTimeUtil.isSameDay;
import static com.whiskels.notifier.utilities.DateTimeUtil.reportDate;

public class DailyReportContext extends ReportContext {
    private static final BiPredicate<Employee, LocalDate> BIRTHDAY_PREDICATE = (employee, date) -> {
        var birthday = employee.getBirthday();
        return isSameDay(birthday, date);
    };

    private static final BiPredicate<Employee, LocalDate> ANNIVERSARY_PREDICATE = (employee, date) -> {
        var appointmentDate = employee.getAppointmentDate();
        return isSameDay(appointmentDate, date);
    };

    public DailyReportContext(String headerPrefix) {
        super(date -> STR."\{headerPrefix}\{reportDate(date)}", _ -> true, BIRTHDAY_PREDICATE, ANNIVERSARY_PREDICATE);
    }
}
