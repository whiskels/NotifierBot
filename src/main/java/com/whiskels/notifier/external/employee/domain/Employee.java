package com.whiskels.notifier.external.employee.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.BIRTHDAY_FORMATTER;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.toLocalDate;
import static java.time.YearMonth.now;

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
    String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM")
    Date birthday;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;
    String status;
    @JsonProperty("status_system")
    String statusSystem;

    @Override
    public String toString() {
       return toBirthdayString();
    }

    public String toWorkAnniversaryString() {
        final int totalWorkingYears = now().getYear() - appointmentDate.getYear();
        return String.format("%s %s (%s)", name, BIRTHDAY_FORMATTER.format(appointmentDate), totalWorkingYears);
    }

    public String toBirthdayString() {
        return String.format("%s %s", name, BIRTHDAY_FORMATTER.format(toLocalDate(birthday)));
    }
}
