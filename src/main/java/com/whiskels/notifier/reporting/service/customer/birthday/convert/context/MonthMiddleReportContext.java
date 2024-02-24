package com.whiskels.notifier.reporting.service.customer.birthday.convert.context;

import com.whiskels.notifier.reporting.service.customer.birthday.convert.ReportContext;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;

import java.time.LocalDate;
import java.util.function.BiPredicate;

import static com.whiskels.notifier.utilities.DateTimeUtil.isLater;
import static com.whiskels.notifier.utilities.DateTimeUtil.isSameMonth;

public class MonthMiddleReportContext extends ReportContext {
    private static final BiPredicate<CustomerBirthdayInfo, LocalDate> BIRTHDAY_PREDICATE = (customer, date) -> {
        var birthday = customer.birthday();
        return isMiddleOfMonth(date) && isSameMonth(birthday, date) && isLater(birthday, date);
    };


    public MonthMiddleReportContext(String header) {
        super(_ignored -> header, date -> !isMiddleOfMonth(date), BIRTHDAY_PREDICATE);
    }

    private static boolean isMiddleOfMonth(LocalDate date) {
        return date.getDayOfMonth() == 15;
    }
}
