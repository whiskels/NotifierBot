package com.whiskels.notifier.external.json.employee;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;
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
}
