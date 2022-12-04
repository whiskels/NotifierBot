package com.whiskels.notifier.external.json.employee;

import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

@UtilityClass
public final class EmployeeUtil {
    public static final String STATUS_SYSTEM_FIRED = "fired";
    public static final String STATUS_DECREE = "Декрет";

    public static final Predicate<Employee> NOT_FIRED = e -> nonNull(e.getStatusSystem()) && !e.getStatusSystem().equals(STATUS_SYSTEM_FIRED);
    public static final Predicate<Employee> NOT_DECREE = e -> nonNull(e.getStatus()) && !e.getStatus().equals(STATUS_DECREE);
    public static final Comparator<EmployeeDto> EMPLOYEE_BIRTHDAY_COMPARATOR = Comparator.comparing(EmployeeDto::getBirthday)
            .thenComparing(EmployeeDto::getName);
    public static final Comparator<EmployeeDto> EMPLOYEE_ANNIVERSARY_COMPARATOR = Comparator.comparing(EmployeeDto::getAppointmentDate)
            .thenComparing(EmployeeDto::getName);
}
