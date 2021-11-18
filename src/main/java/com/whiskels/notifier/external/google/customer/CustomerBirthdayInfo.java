package com.whiskels.notifier.external.google.customer;

import com.whiskels.notifier.common.datetime.HasBirthday;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.BIRTHDAY_FORMATTER;
import static java.lang.String.format;

@Value
@Builder
public class CustomerBirthdayInfo implements HasBirthday {
    String name;
    String companyName;
    LocalDate birthday;

    public String toString() {
        return format("%s %s (%s)", name, BIRTHDAY_FORMATTER.format(birthday), companyName);
    }
}
