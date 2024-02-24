package com.whiskels.notifier.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whiskels.notifier.utilities.formatters.DateTimeFormatter.BIRTHDAY_FORMAT;
import static com.whiskels.notifier.utilities.formatters.DateTimeFormatter.DAY_MONTH_YEAR_FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {
    // https://stackoverflow.com/a/33943576/13716599
    public static LocalDate subtractWorkingDays(LocalDate date, int workdays) {
        return calculateWorkingDays(date, workdays, false);
    }

    private static LocalDate calculateWorkingDays(LocalDate date, int workdays, boolean add) {
        if (workdays < 1) {
            return date;
        }

        LocalDate result = date;
        int addedDays = 0;
        while (addedDays < workdays) {
            result = add ? result.plusDays(1) : result.minusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++addedDays;
            }
        }

        return result;
    }

    public static String reportDate(LocalDate reportDate) {
        return STR." \{DAY_MONTH_YEAR_FORMATTER.format(reportDate)}";
    }

    public static LocalDate parseDate(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text, new DateTimeFormatterBuilder()
                    .appendPattern(BIRTHDAY_FORMAT)
                    .parseDefaulting(ChronoField.YEAR, 2020)
                    .toFormatter(Locale.ENGLISH));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isSameMonth(LocalDate date, LocalDate checkDate) {
        return date != null && date.getMonth().equals(checkDate.getMonth());
    }

    public static boolean isSameDay(LocalDate date, LocalDate compareDate) {
        return date != null && date.withYear(compareDate.getYear()).equals(compareDate);
    }

    public static boolean isLater(LocalDate date, LocalDate checkDate) {
        return date.getDayOfMonth() >= checkDate.getDayOfMonth();
    }

    public static <T> Predicate<T> isSameDay(Function<T, LocalDate> func, LocalDate compareDate) {
        return e -> func.apply(e).withYear(compareDate.getYear()).equals(compareDate);
    }

    public static <T> Predicate<T> isSameMonth(Function<T, LocalDate> func, LocalDate today) {
        return e -> isSameMonth(func.apply(e), today);
    }
}
