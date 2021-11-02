package com.whiskels.notifier.external.employee;

import com.whiskels.notifier.external.InMemoryDataLoader;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.StreamUtil.filter;
import static com.whiskels.notifier.external.employee.EmployeeUtil.NOT_DECREE;
import static com.whiskels.notifier.external.employee.EmployeeUtil.NOT_FIRED;

@Component
@ConditionalOnProperty("external.employee.url")
public class EmployeeInMemoryLoader extends InMemoryDataLoader<Employee> implements TelegramLabeled {
    @SuppressWarnings("unchecked")
    private static final Predicate<Employee>[] EMPLOYEE_FILTERS = new Predicate[]{NOT_DECREE, NOT_FIRED};

    @Value("${external.employee.telegram.label:Employees}")
    private String telegramLabel;

    public EmployeeInMemoryLoader(@Value("${external.employee.url}") String jsonUrl) {
        super(jsonUrl);
    }

    @Scheduled(cron = "${external.employee.cron:0 30 8 * * MON-FRI}", zone = "${common.timezone}")
    public void updateScheduled() {
        update();
    }

    @Override
    protected List<Employee> loadData() {
        return filter(loadFromJson(), EMPLOYEE_FILTERS);
    }

    @Override
    public String getLabel() {
        return telegramLabel;
    }
}
