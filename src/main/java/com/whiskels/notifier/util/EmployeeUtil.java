package com.whiskels.notifier.util;

import com.whiskels.notifier.model.Employee;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.function.Predicate;

import static com.whiskels.notifier.util.DateTimeUtil.toLocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmployeeUtil {
    public static final String STATUS_SYSTEM_FIRED = "fired";
    public static final String STATUS_DECREE = "Декрет";

    public static final Predicate<Employee> NOT_FIRED = e -> e.getStatusSystem() != null && !e.getStatusSystem().equals(STATUS_SYSTEM_FIRED);
    public static final Predicate<Employee> NOT_DECREE = e -> e.getStatus() != null && !e.getStatus().equals(STATUS_DECREE);
    public static final Predicate<Employee> BIRTHDAY_NOT_NULL = e -> e.getBirthday() != null;
    public static final Comparator<Employee> BIRTHDAY_COMPARATOR = Comparator.comparing(Employee::getBirthday)
            .thenComparing(Employee::getName);

    public static long daysBetweenBirthdayAnd(Employee employee, LocalDate today) {
        return ChronoUnit.DAYS.between(
                today.atStartOfDay(),
                toLocalDate(employee.getBirthday()).withYear(today.getYear()).atStartOfDay());
    }

    public static Predicate<Employee> isBirthdayOn(LocalDate date) {
        return employee -> daysBetweenBirthdayAnd(employee, date) == 0;
    }

    public static Predicate<Employee> isBirthdayNextWeekFrom(LocalDate date) {
        return employee -> {
            long daysUntilBirthday = daysBetweenBirthdayAnd(employee, date);
            return daysUntilBirthday > 0 && daysUntilBirthday <= 7;
        };
    }

    public static Predicate<Employee> isBirthdaySameMonth(LocalDate today) {
        return employee -> toLocalDate(employee.getBirthday()).getMonth().equals(today.getMonth());
    }
}
