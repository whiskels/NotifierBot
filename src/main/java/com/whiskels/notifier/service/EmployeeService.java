package com.whiskels.notifier.service;

import com.whiskels.notifier.model.Employee;
import com.whiskels.notifier.util.JsonUtil;
import com.whiskels.notifier.util.ReportBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.util.DateTimeUtil.todayWithOffset;
import static com.whiskels.notifier.util.EmployeeUtil.*;
import static com.whiskels.notifier.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.util.StreamUtil.filterAndSort;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService extends AbstractJSONService {
    private static final String BIRTHDAY_REPORT_HEADER = "Birthdays";
    private static final String BIRTHDAY_MONTHLY_REPORT_HEADER = "Birthdays monthly status";
    private static final String NO_DATA = "Nobody";
    private static final String UPCOMING_WEEK = "*Upcoming week:*";
    private static final List<Predicate<Employee>> EMPLOYEE_FILTERS = List.of(NOT_DECREE, NOT_FIRED, BIRTHDAY_NOT_NULL);

    @Value("${json.employee.url}")
    private String employeeUrl;

    @Getter
    private List<Employee> employeeList;

    @PostConstruct
    private void initEmployeeList() {
        update();
    }

    /**
     * Reads JSON data from URL and creates Customer list
     */
    @Scheduled(cron = "${json.employee.cron}")
    protected void update() {
        log.info("updating employee list");
        employeeList = filterAndSort(readFromJson(employeeUrl), EMPLOYEE_FILTERS);
    }

    public String dailyMessage() {
        final LocalDate today = todayWithOffset(serverHourOffset);
        return ReportBuilder.withHeader(BIRTHDAY_REPORT_HEADER, today)
                .setNoData(NO_DATA)
                .list(employeeList, isBirthdayOn(today), COLLECTOR_COMMA_SEPARATED)
                .line()
                .line(UPCOMING_WEEK)
                .list(employeeList, isBirthdayNextWeekFrom(today), COLLECTOR_COMMA_SEPARATED)
                .build();
    }

    public String monthlyMessage() {
        final LocalDate today = todayWithOffset(serverHourOffset);
        return ReportBuilder.withHeader(BIRTHDAY_MONTHLY_REPORT_HEADER, today)
                .setNoData(NO_DATA)
                .list(employeeList, isBirthdaySameMonth(today), COLLECTOR_COMMA_SEPARATED)
                .build();
    }

    private List<Employee> readFromJson(String url) {
        return JsonUtil.readValuesFromUrl(url, Employee.class);
    }
}