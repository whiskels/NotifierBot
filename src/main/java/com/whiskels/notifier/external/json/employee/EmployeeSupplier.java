package com.whiskels.notifier.external.json.employee;

import com.whiskels.notifier.external.InMemorySupplier;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("external.employee.url")
public class EmployeeSupplier extends InMemorySupplier<Employee> implements TelegramLabeled {
    @Value("${external.employee.telegram.label:Employees}")
    private String telegramLabel;

    @Scheduled(cron = "${external.employee.cron:0 30 8 * * MON-FRI}", zone = "${common.timezone}")
    public void updateScheduled() {
        update();
    }

    @Override
    public String getLabel() {
        return telegramLabel;
    }
}
