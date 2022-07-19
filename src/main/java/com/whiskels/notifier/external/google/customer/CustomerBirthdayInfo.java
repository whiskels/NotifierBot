package com.whiskels.notifier.external.google.customer;

import com.whiskels.notifier.common.datetime.HasBirthday;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.BIRTHDAY_FORMATTER;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.parseDate;
import static java.lang.String.format;

@Value
@Builder
public class CustomerBirthdayInfo implements HasBirthday {
    String responsible;
    String responsibleEmail;
    String clientId;
    String company;
    String name;
    String surname;
    String email;
    String telegram;
    String phone;
    LocalDate birthday;
    String position;


    //TODO search for a better way of excel parsing - maybe query?
    public static CustomerBirthdayInfo fromExcelData(List<Object> data) {
        if (data.size() < 11) return null;
        try {
            return CustomerBirthdayInfo.builder()
                    .responsible((String) data.get(0))
                    .responsibleEmail((String) data.get(1))
                    .clientId((String) data.get(2))
                    .company((String) data.get(3))
                    .name((String) data.get(4))
                    .surname((String) data.get(5))
                    .email((String) data.get(6))
                    .telegram((String) data.get(7))
                    .phone((String) data.get(8))
                    .birthday(parseDate((String) data.get(9)))
                    .position((String) data.get(10))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        return format("%s %s %s (%s)", name, surname, BIRTHDAY_FORMATTER.format(birthday), company);
    }
}
