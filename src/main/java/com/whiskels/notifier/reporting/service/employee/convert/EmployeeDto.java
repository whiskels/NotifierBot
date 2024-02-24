package com.whiskels.notifier.reporting.service.employee.convert;

import com.whiskels.notifier.reporting.domain.HasBirthday;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import lombok.Builder;

import java.time.Clock;
import java.time.LocalDate;

import static com.whiskels.notifier.utilities.formatters.DateTimeFormatter.BIRTHDAY_FORMATTER;

@Builder
record EmployeeDto(String name, LocalDate birthday, LocalDate appointmentDate) implements HasBirthday {
    public static EmployeeDto from(Employee e) {
        return EmployeeDto.builder()
                .name(e.name())
                .appointmentDate(e.getAppointmentDate())
                .birthday(e.birthday())
                .build();
    }

    public String toBirthdayString() {
        return STR."\{name} \{BIRTHDAY_FORMATTER.format(birthday)}";
    }

    public String toWorkAnniversaryString(Clock clock) {
        final int totalWorkingYears = LocalDate.now(clock).getYear() - appointmentDate.getYear();
        return STR."\{name} \{BIRTHDAY_FORMATTER.format(appointmentDate)} (\{totalWorkingYears})";
    }
}
