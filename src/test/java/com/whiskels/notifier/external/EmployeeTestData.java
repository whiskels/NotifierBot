package com.whiskels.notifier.external;

import com.whiskels.notifier.external.employee.domain.Employee;
import lombok.experimental.UtilityClass;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.util.Calendar.JANUARY;

@UtilityClass
public class EmployeeTestData {
    public static String EMPLOYEE_JSON = "json/external/employee.json";

    public static Employee employeeWorking() {
        Employee employee = new Employee();
        employee.setName("Jason Bourne");
        employee.setBirthday(new GregorianCalendar(1970, JANUARY, 1).getTime());
        employee.setStatus("Работает");
        employee.setStatusSystem("working");
        return employee;
    }

    public static Employee employeeDecree() {
        Employee employee = new Employee();
        employee.setName("Test employee");
        employee.setBirthday(new GregorianCalendar(1970, JANUARY, 31).getTime());
        employee.setStatus("Декрет");
        employee.setStatusSystem("decree");
        return employee;
    }

    public static Employee employeeFired() {
        Employee employee = new Employee();
        employee.setName("James Bond");
        employee.setBirthday(new GregorianCalendar(1970, Calendar.AUGUST, 25).getTime());
        employee.setStatus("Уволен");
        employee.setStatusSystem("fired");
        return employee;
    }
}
