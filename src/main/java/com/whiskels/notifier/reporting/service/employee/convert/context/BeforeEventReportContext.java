

package com.whiskels.notifier.reporting.service.employee.convert.context;

import com.whiskels.notifier.reporting.service.employee.convert.ReportContext;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;

import java.time.LocalDate;
import java.util.function.BiPredicate;

import static com.whiskels.notifier.utilities.DateTimeUtil.isSameDay;
import static com.whiskels.notifier.utilities.DateTimeUtil.isSameMonth;
import static com.whiskels.notifier.utilities.DateTimeUtil.reportDate;

public class BeforeEventReportContext extends ReportContext {
    public BeforeEventReportContext(String headerPrefix, int daysBefore) {
        super(date -> STR."\{headerPrefix}\{reportDate(date)}",
                false,
                (employee, date) -> {
                    var birthday = employee.getBirthday();
                    return isSameDay(birthday, date.plusDays(daysBefore));
                }
                , (employee, date) -> {
                    var appointmentDate = employee.getAppointmentDate();
                    return isSameDay(appointmentDate, date.plusDays(daysBefore));
                });
    }
}
