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

    public static long daysBetweenBirthdayAnd(Employee employee, LocalDate today) {
        return ChronoUnit.DAYS.between(
                today.atStartOfDay(),
                toLocalDate(employee.getBirthday()).withYear(today.getYear()).atStartOfDay());
    }

    public static long daysBetweenAnniversaryAnd(Employee employee, LocalDate today) {
        return ChronoUnit.DAYS.between(
                today.atStartOfDay(),
                employee.getAppointmentDate().withYear(today.getYear()).atStartOfDay());
    }

    public static Predicate<Employee> isBirthdayOn(LocalDate date) {
        return employee -> daysBetweenBirthdayAnd(employee, date) == 0;
    }

    public static Predicate<Employee> isBirthdayBetween(LocalDate startDate, LocalDate endDate) {
        return employee -> daysBetweenBirthdayAnd(employee, startDate) >= 0
                && daysBetweenBirthdayAnd(employee, endDate) < 0;
    }

    public static Predicate<Employee> isBirthdaySameMonth(LocalDate today) {
        return employee -> toLocalDate(employee.getBirthday()).getMonth().equals(today.getMonth());
    }

    public static Predicate<Employee> isBirthdayLaterThan(LocalDate today) {
        return employee -> toLocalDate(employee.getBirthday()).getDayOfMonth() >= today.getDayOfMonth();
    }

    public static Predicate<Employee> isAnniversarySameMonth(LocalDate today) {
        return employee -> {
            final LocalDate appointmentDate = employee.getAppointmentDate();
            return (appointmentDate != null && appointmentDate.getMonth().equals(today.getMonth()));
        };
    }

    public static Predicate<Employee> isAnniversaryLaterThan(LocalDate today) {
        return employee -> {
            final LocalDate appointmentDate = employee.getAppointmentDate();
            return (appointmentDate != null && appointmentDate.getDayOfMonth() >= today.getDayOfMonth());
        };
    }

    public static Predicate<Employee> isAnniversaryBetween(LocalDate startDate, LocalDate endDate) {
        return employee -> daysBetweenAnniversaryAnd(employee, startDate) >= 0
                && daysBetweenAnniversaryAnd(employee, endDate) < 0;
    }

}
