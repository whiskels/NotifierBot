package com.whiskels.notifier.reporting.service.employee.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.whiskels.notifier.reporting.domain.HasBirthday;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.whiskels.notifier.utilities.formatters.DateTimeFormatter.BIRTHDAY_FORMAT;

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
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Employee implements HasBirthday {
    private String name;
    @JsonDeserialize(using = BirthdayDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BIRTHDAY_FORMAT)
    private LocalDate birthday;
    @JsonProperty("appointment_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;
    private String status;
    @JsonProperty("status_system")
    private String statusSystem;

    @Override
    public String name() {
        return name;
    }

    @Override
    public LocalDate birthday() {
        return birthday;
    }
}
