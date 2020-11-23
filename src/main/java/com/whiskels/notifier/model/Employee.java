package com.whiskels.notifier.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * Employee data is received from JSON of the following syntax:
 * [{"row_number":1,
 * "id":1111,
 * "name":"Company employee",
 * "employee_id":111,
 * "full_name":"Employee full name",
 * "email":"employee111@company.com",
 * "appointment_date":"2020-10-10",
 * "removal_date":null,
 * "phone":"896812345678",
 * "department":"Sales",
 * "position":"Sales Manager",
 * "grade":"Senior",
 * "birthday":"01.01",
 * "status":"\u0420\u0430\u0431\u043e\u0442\u0430\u0435\u0442",
 * "status_system":"working",
 * "office":"Moscow",
 * "is_new_employee":false},...
 */
@Data
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {
public static final String STATUS_SYSTEM_FIRED = "fired";
public static final String STATUS_DECREE = "Декрет";


//    @JsonProperty("id")
//    private int id;
    @JsonProperty("name")
    private String name;
//    @JsonProperty("employee_id")
//    private int employeeId;
//    @JsonProperty("full_name")
//    private String fullName;
//    @JsonProperty("email")
//    private String email;
//    @JsonProperty("appointment_date")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    private LocalDate appointmentDate;
//    @JsonProperty("removal_date")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    private LocalDate removalDate;
//    @JsonProperty("phone")
//    private String phone;
//    @JsonProperty("department")
//    private String department;
//    @JsonProperty("position")
//    private String position;
//    @JsonProperty("grade")
//    private String grade;
    @JsonProperty("birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM")
    private Date birthday;
    @JsonProperty("status")
    private String status;
    @JsonProperty("status_system")
    private String statusSystem;
//    @JsonProperty("office")
//    private String office;
//    @JsonProperty("is_new_employee")
//    private boolean isNew;
}
