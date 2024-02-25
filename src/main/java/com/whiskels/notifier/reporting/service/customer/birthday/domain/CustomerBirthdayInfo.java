package com.whiskels.notifier.reporting.service.customer.birthday.domain;

import com.whiskels.notifier.reporting.domain.HasBirthday;
import lombok.Builder;

import java.time.LocalDate;

import static com.whiskels.notifier.utilities.Util.defaultIfNull;
import static com.whiskels.notifier.utilities.formatters.DateTimeFormatter.BIRTHDAY_FORMATTER;
import static java.lang.String.format;

@Builder
public record CustomerBirthdayInfo(String responsible, String responsibleEmail,
                                   String clientId, String company, String name,
                                   String surname, String email, String telegram,
                                   String phone, LocalDate birthday,
                                   String position) implements HasBirthday {
    public String toString() {
        return format("%s %s %s (%s)", name, surname, defaultIfNull(BIRTHDAY_FORMATTER.format(birthday), "unknown"), company);
    }
}
