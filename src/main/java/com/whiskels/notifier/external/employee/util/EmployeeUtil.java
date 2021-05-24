package com.whiskels.notifier.external.employee.util;

import com.whiskels.notifier.external.employee.domain.Employee;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmployeeUtil {
    public static final String STATUS_SYSTEM_FIRED = "fired";
    public static final String STATUS_DECREE = "Декрет";

    public static final Predicate<Employee> NOT_FIRED = e -> e.getStatusSystem() != null && !e.getStatusSystem().equals(STATUS_SYSTEM_FIRED);
    public static final Predicate<Employee> NOT_DECREE = e -> e.getStatus() != null && !e.getStatus().equals(STATUS_DECREE);
    public static final Comparator<Employee> EMPLOYEE_BIRTHDAY_COMPARATOR = Comparator.comparing(Employee::getBirthday)
            .thenComparing(Employee::getName);
    public static final Comparator<Employee> EMPLOYEE_ANNIVERSARY_COMPARATOR = Comparator.comparing(Employee::getAppointmentDate)
            .thenComparing(Employee::getName);

    public static Predicate<Employee> notNull(Function<Employee, LocalDate> func) {
        return e -> func.apply(e) != null;
    }

    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(
                endDate.atStartOfDay(),
                startDate.withYear(endDate.getYear()).atStartOfDay());
    }

    public static boolean isSameMonth(LocalDate date, LocalDate checkDate) {
        return date.getMonth().equals(checkDate.getMonth());
    }

    public static boolean isLater(LocalDate date, LocalDate checkDate) {
        return date.getDayOfMonth() >= checkDate.getDayOfMonth();
    }

    public static Predicate<Employee> isSameDay(Function<Employee, LocalDate> func, LocalDate compareDate) {
        return e -> func.apply(e).withYear(compareDate.getYear()).equals(compareDate);
    }

    public static Predicate<Employee> isBetween(Function<Employee, LocalDate> func, LocalDate startDate, LocalDate endDate) {
        return e -> daysBetween(func.apply(e), startDate) > 0
                && daysBetween(func.apply(e), endDate) <= 0;
    }

    public static Predicate<Employee> isSameMonth(Function<Employee, LocalDate> func, LocalDate today) {
        return e -> isSameMonth(func.apply(e), today);
    }

    public static Predicate<Employee> isLaterThan(Function<Employee, LocalDate> func, LocalDate today) {
        return e -> isLater(func.apply(e), today);
    }
}
