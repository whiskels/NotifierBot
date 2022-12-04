package com.whiskels.notifier.external.google.customer;

import com.whiskels.notifier.common.datetime.HasBirthday;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

import static com.whiskels.notifier.common.util.DateTimeUtil.BIRTHDAY_FORMATTER;
import static java.lang.String.format;

@Value
@Builder
public class CustomerBirthdayInfoDto implements HasBirthday {
    String company;
    String name;
    String surname;
    LocalDate birthday;

    public static CustomerBirthdayInfoDto from(CustomerBirthdayInfo c) {
        return CustomerBirthdayInfoDto.builder()
                .company(c.getCompany())
                .name(c.getName())
                .surname(c.getSurname())
                .birthday(c.getBirthday())
                .build();
    }

    public String toString() {
        return format("%s %s %s (%s)", name, surname, BIRTHDAY_FORMATTER.format(birthday), company);
    }
}
