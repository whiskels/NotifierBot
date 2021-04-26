package com.whiskels.notifier.common.datetime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {
    public static final DateTimeFormatter DAY_MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter YEAR_MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM");

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
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Europe/Moscow")).toLocalDate();
    }

    public static String format(LocalDateTime ldt) {
        return ldt.toString().replace('T', ' ');
    }

    public static String reportDate(LocalDate reportDate) {
        return " " + DAY_MONTH_YEAR_FORMATTER.format(reportDate);
    }
}
