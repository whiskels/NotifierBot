package com.whiskels.notifier.reporting.service.customer.birthday.convert;

import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public class ReportContext {
    private final Function<LocalDate, String> headerMapper;
    /**
     * Determines whether report creation should be skipped if predicate returns false
     */
    private final Predicate<LocalDate> skipEmptyPredicate;
    private final BiPredicate<CustomerBirthdayInfo, LocalDate> predicate;
}
