package com.whiskels.notifier.common.datetime;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public interface DateTimeProvider {
    // https://stackoverflow.com/a/33943576/13716599
    default LocalDate subtractWorkingDays(LocalDate date, int amount) {
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

    default LocalDate toLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    default LocalDate today() {
        return now().toLocalDate();
    }

    default LocalDateTime now() {
        return LocalDateTime.now();
    }
}
