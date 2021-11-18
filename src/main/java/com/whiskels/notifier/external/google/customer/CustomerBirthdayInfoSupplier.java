package com.whiskels.notifier.external.google.customer;

import com.whiskels.notifier.external.InMemorySupplier;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("external.google.customer.birthday.spreadsheet")
public class CustomerBirthdayInfoSupplier extends InMemorySupplier<CustomerBirthdayInfo> implements TelegramLabeled {
    @Value("${external.google.customer.telegram.label:Customer birthdays}")
    private String telegramLabel;

    @Scheduled(cron = "${external.google.customer.birthday.cron:0 30 8 * * MON-FRI}", zone = "${common.timezone}")
    public void updateScheduled() {
        update();
    }

    @Override
    public String getLabel() {
        return telegramLabel;
    }
}
