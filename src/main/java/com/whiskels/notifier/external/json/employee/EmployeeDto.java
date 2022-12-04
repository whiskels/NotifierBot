package com.whiskels.notifier.external.json.employee;

import com.whiskels.notifier.common.datetime.HasBirthday;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

import static com.whiskels.notifier.common.util.DateTimeUtil.BIRTHDAY_FORMATTER;
import static java.lang.String.format;
import static java.time.YearMonth.now;

@Value
@Builder
public class EmployeeDto implements HasBirthday {
    String name;
    LocalDate birthday;
    LocalDate appointmentDate;

    public static EmployeeDto from(Employee e) {
        return EmployeeDto.builder()
                .name(e.getName())
                .appointmentDate(e.getAppointmentDate())
                .birthday(e.getBirthday())
                .build();
    }

    public String toBirthdayString() {
        return birthday == null
                ? name + " birthday is unknown"
                : format("%s %s", name, BIRTHDAY_FORMATTER.format(birthday));
    }

    public String toWorkAnniversaryString() {
        if (appointmentDate == null) {
            return name + " (appointment date not set)";
        }
        final int totalWorkingYears = now().getYear() - appointmentDate.getYear();
        return format("%s %s (%s)", name, BIRTHDAY_FORMATTER.format(appointmentDate), totalWorkingYears);
    }
}
