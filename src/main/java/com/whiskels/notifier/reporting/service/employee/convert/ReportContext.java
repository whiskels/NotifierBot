package com.whiskels.notifier.reporting.service.employee.convert;

import com.whiskels.notifier.reporting.service.employee.domain.Employee;
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
    private final Predicate<LocalDate> skipEmpty;
    private final BiPredicate<Employee, LocalDate> birthdayPredicate;
    private final BiPredicate<Employee, LocalDate>  anniversaryPredicate;
}
