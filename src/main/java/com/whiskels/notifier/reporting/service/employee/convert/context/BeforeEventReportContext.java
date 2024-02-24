

package com.whiskels.notifier.reporting.service.employee.convert.context;

import com.whiskels.notifier.reporting.service.employee.convert.ReportContext;

import static com.whiskels.notifier.utilities.DateTimeUtil.isSameDay;
import static com.whiskels.notifier.utilities.DateTimeUtil.reportDate;

public class BeforeEventReportContext extends ReportContext {
    public BeforeEventReportContext(String headerPrefix, int daysBefore) {
        super(date -> STR."\{headerPrefix}\{reportDate(date)}",
                _ -> true,
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
