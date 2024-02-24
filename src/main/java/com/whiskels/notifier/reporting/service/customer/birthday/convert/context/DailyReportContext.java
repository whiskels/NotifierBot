package com.whiskels.notifier.reporting.service.customer.birthday.convert.context;

import com.whiskels.notifier.reporting.service.customer.birthday.convert.ReportContext;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;

import java.time.LocalDate;
import java.util.function.BiPredicate;

import static com.whiskels.notifier.utilities.DateTimeUtil.isSameDay;
import static com.whiskels.notifier.utilities.DateTimeUtil.reportDate;

public class DailyReportContext extends ReportContext {
    private static final BiPredicate<CustomerBirthdayInfo, LocalDate> BIRTHDAY_PREDICATE = (customer, date) -> {
        var birthday = customer.birthday();
        return isSameDay(birthday, date);
    };

    public DailyReportContext(String headerPrefix) {
        super(date -> STR."\{headerPrefix}\{reportDate(date)}", _ignored -> true, BIRTHDAY_PREDICATE);
    }
}
