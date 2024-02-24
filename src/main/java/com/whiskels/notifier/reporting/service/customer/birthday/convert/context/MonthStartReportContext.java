package com.whiskels.notifier.reporting.service.customer.birthday.convert.context;

import com.whiskels.notifier.reporting.service.customer.birthday.convert.ReportContext;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;

import java.time.LocalDate;
import java.util.function.BiPredicate;

import static com.whiskels.notifier.utilities.DateTimeUtil.isSameMonth;

public class MonthStartReportContext extends ReportContext {
    private static final BiPredicate<CustomerBirthdayInfo, LocalDate> BIRTHDAY_PREDICATE = (customer, date) -> {
        var birthday = customer.birthday();
        return isStartOfMonth(date) && isSameMonth(birthday, date);
    };


    public MonthStartReportContext(String header) {
        super(_ignored -> header, date -> !isStartOfMonth(date), BIRTHDAY_PREDICATE);
    }

    private static boolean isStartOfMonth(LocalDate date) {
        return date.getDayOfMonth() == 1;
    }
}
