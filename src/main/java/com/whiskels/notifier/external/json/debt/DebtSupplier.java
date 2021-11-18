package com.whiskels.notifier.external.json.debt;

import com.whiskels.notifier.external.InMemorySupplier;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("external.customer.debt.url")
public class DebtSupplier extends InMemorySupplier<Debt> implements TelegramLabeled {
    @Value("${external.customer.debt.telegram.label:Debt loader}")
    private String telegramLabel;

    @Scheduled(cron = "${external.customer.debt.cron:0 55 11 * * MON-FRI}", zone = "${common.timezone}")
    public void updateScheduled() {
        update();
    }

    @Override
    public String getLabel() {
        return telegramLabel;
    }
}
