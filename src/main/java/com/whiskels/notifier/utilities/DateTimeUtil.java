package com.whiskels.notifier.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whiskels.notifier.utilities.formatters.DateTimeFormatter.DAY_MONTH_YEAR_FORMATTER;
import static com.whiskels.notifier.utilities.formatters.DateTimeFormatter.FORMATTER_WITHOUT_YEAR;
import static com.whiskels.notifier.utilities.formatters.DateTimeFormatter.FORMATTER_WITH_YEAR;
import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
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
        LocalDate result = null;
        if (text == null || text.isEmpty() || text.equals("-")) {
            log.debug("Date is empty - skipping parsing");
            return null;
        }
        List<DateTimeFormatter> formatterList = List.of(FORMATTER_WITH_YEAR, FORMATTER_WITHOUT_YEAR);
        for (DateTimeFormatter formatter : formatterList) {
            try {
                result = LocalDate.parse(text, formatter);
                log.debug("Parsed {} to date: {}", text, result);
                break;
            } catch (Exception _) {
            }
        }
        if (isNull(result)) {
            log.warn("Unable to deserialize date: {}", text);
        }
        return result;
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
