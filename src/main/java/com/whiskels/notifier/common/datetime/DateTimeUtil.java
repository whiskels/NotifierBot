package com.whiskels.notifier.common.datetime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {
    public static final DateTimeFormatter DAY_MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter YEAR_MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM");

    public static <T extends HasBirthday> Comparator<T> birthdayComparator() {
        return Comparator.comparing(T::getBirthday).thenComparing(T::getName);
    }

    // https://stackoverflow.com/a/33943576/13716599
    public static LocalDate subtractWorkingDays(LocalDate date, int workdays) {
        return calculateWorkingDays(date, workdays, false);
    }

    public static LocalDate addWorkingDays(LocalDate date, int workdays) {
        return calculateWorkingDays(date, workdays, true);
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

    public static LocalDate toLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Europe/Moscow")).toLocalDate();
    }

    public static String format(LocalDateTime ldt) {
        return ldt.toString().replace('T', ' ');
    }

    public static String reportDate(LocalDate reportDate) {
        return " " + DAY_MONTH_YEAR_FORMATTER.format(reportDate);
    }

    public static LocalDate parseDate(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text, new DateTimeFormatterBuilder()
                    .appendPattern("dd.MM")
                    .parseDefaulting(ChronoField.YEAR, 2020)
                    .toFormatter(Locale.ENGLISH));
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> Predicate<T> notNull(Function<T, LocalDate> func) {
        return e -> func.apply(e) != null;
    }

    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(
                endDate.atStartOfDay(),
                startDate.withYear(endDate.getYear()).atStartOfDay());
    }

    public static boolean isSameMonth(LocalDate date, LocalDate checkDate) {
        return date != null && date.getMonth().equals(checkDate.getMonth());
    }

    public static boolean isLater(LocalDate date, LocalDate checkDate) {
        return date.getDayOfMonth() >= checkDate.getDayOfMonth();
    }

    public static <T> Predicate<T> isSameDay(Function<T, LocalDate> func, LocalDate compareDate) {
        return e -> func.apply(e).withYear(compareDate.getYear()).equals(compareDate);
    }

    public static <T> Predicate<T> isBetween(Function<T, LocalDate> func, LocalDate startDate, LocalDate endDate) {
        return e -> daysBetween(func.apply(e), startDate) > 0
                && daysBetween(func.apply(e), endDate) <= 0;
    }

    public static <T> Predicate<T> isSameMonth(Function<T, LocalDate> func, LocalDate today) {
        return e -> isSameMonth(func.apply(e), today);
    }

    public static <T> Predicate<T> isLaterThan(Function<T, LocalDate> func, LocalDate today) {
        return e -> isLater(func.apply(e), today);
    }
}
