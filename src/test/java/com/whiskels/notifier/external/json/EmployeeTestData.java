package com.whiskels.notifier.external.json;

import com.whiskels.notifier.external.json.employee.Employee;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class EmployeeTestData {
    public static String EMPLOYEE_JSON = "json/external/employee.json";

    public static Employee employeeWorking() {
        Employee employee = new Employee();
        employee.setName("Jason Bourne");
        employee.setBirthday(LocalDate.of(2020, 1, 1));
        employee.setAppointmentDate(LocalDate.of(2020,10,14));
        employee.setStatus("Работает");
        employee.setStatusSystem("working");
        return employee;
    }

    public static Employee employeeDecree() {
        Employee employee = new Employee();
        employee.setName("Test employee");
        employee.setBirthday(LocalDate.of(2020, 1, 31));
        employee.setAppointmentDate(LocalDate.of(2019,8,1));
        employee.setStatus("Декрет");
        employee.setStatusSystem("decree");
        return employee;
    }

    public static Employee employeeFired() {
        Employee employee = new Employee();
        employee.setName("James Bond");
        employee.setBirthday(LocalDate.of(2020, 8, 25));
        employee.setAppointmentDate(LocalDate.of(2016,10,17));
        employee.setStatus("Уволен");
        employee.setStatusSystem("fired");
        return employee;
    }

    public static Employee employeeNullBirthday() {
        Employee employee = new Employee();
        employee.setName("Loki");
        employee.setStatus("Работает");
        employee.setStatusSystem("working");
        employee.setAppointmentDate(LocalDate.of(2003,2,11));
        return employee;
    }
}
