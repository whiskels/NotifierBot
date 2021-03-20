package com.whiskels.notifier.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {
    // https://stackoverflow.com/a/33943576/13716599
    public static LocalDate subtractWorkingDays(LocalDate date, int amount) {
        if (amount < 1) {
            return date;
        }

        LocalDate result = date;
        int addedDays = 0;
        while (addedDays < amount) {
            result = result.minusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++addedDays;
            }
        }

        return result;
    }

    public static LocalDate toLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate todayWithOffset(int serverHourOffset) {
        return LocalDateTime.now().plusHours(serverHourOffset).toLocalDate();
    }
}
