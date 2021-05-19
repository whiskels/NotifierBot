package com.whiskels.notifier.external.employee.util;

import com.whiskels.notifier.external.employee.domain.Employee;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.toLocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmployeeUtil {
    public static final String STATUS_SYSTEM_FIRED = "fired";
    public static final String STATUS_DECREE = "Декрет";

    public static final Predicate<Employee> NOT_FIRED = e -> e.getStatusSystem() != null && !e.getStatusSystem().equals(STATUS_SYSTEM_FIRED);
    public static final Predicate<Employee> NOT_DECREE = e -> e.getStatus() != null && !e.getStatus().equals(STATUS_DECREE);
    public static final Predicate<Employee> BIRTHDAY_NOT_NULL = e -> e.getBirthday() != null;

    public static final Comparator<Employee> EMPLOYEE_BIRTHDAY_COMPARATOR = Comparator.comparing(Employee::getBirthday)
            .thenComparing(Employee::getName);
    public static final Comparator<Employee> EMPLOYEE_ANNIVERSARY_COMPARATOR = Comparator.comparing(Employee::getAppointmentDate)
            .thenComparing(Employee::getName);

    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(
                endDate.atStartOfDay(),
                startDate.withYear(endDate.getYear()).atStartOfDay());
    }

    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return date != null && daysBetween(date, startDate) > 0 && daysBetween(startDate, endDate) <= 0;
    }

    public static boolean isSameMonth(LocalDate date, LocalDate checkDate) {
        return date != null && date.getMonth().equals(checkDate.getMonth());
    }

    public static boolean isLater(LocalDate date, LocalDate checkDate) {
        return date != null && date.getDayOfMonth() >= checkDate.getDayOfMonth();
    }

    public static Predicate<Employee> isBirthdayBetween(LocalDate startDate, LocalDate endDate) {
        return e -> daysBetween(getBirthday(e), startDate) > 0
                && daysBetween(getBirthday(e), endDate) <= 0;
    }

    public static Predicate<Employee> isBirthdaySameMonth(LocalDate today) {
        return e -> isSameMonth(getBirthday(e), today);
    }

    public static Predicate<Employee> isBirthdayLaterThan(LocalDate today) {
        return e -> isLater(getBirthday(e), today);
    }

    public static Predicate<Employee> isAnniversarySameMonth(LocalDate today) {
        return e -> isSameMonth(e.getAppointmentDate(), today);
    }

    public static Predicate<Employee> isAnniversaryLaterThan(LocalDate today) {
        return e -> isLater(e.getAppointmentDate(), today);
    }

    public static Predicate<Employee> isAnniversaryBetween(LocalDate startDate, LocalDate endDate) {
        return e -> isBetween(e.getAppointmentDate(), startDate, endDate);
    }
    
    public static LocalDate getBirthday(Employee e) {
        return toLocalDate(e.getBirthday());
    }
}
