

package com.whiskels.notifier.reporting.service.customer.birthday.convert.context;


import com.whiskels.notifier.reporting.service.customer.birthday.convert.ReportContext;

import static com.whiskels.notifier.utilities.DateTimeUtil.isSameDay;
import static com.whiskels.notifier.utilities.DateTimeUtil.reportDate;

public class BeforeEventReportContext extends ReportContext {
    public BeforeEventReportContext(String headerPrefix, int daysBefore) {
        super(date -> STR."\{headerPrefix}\{reportDate(date)}",
                _ignored -> true,
                (customer, date) -> {
                    var birthday = customer.birthday();
                    return isSameDay(birthday, date.plusDays(daysBefore));
                });
    }
}
