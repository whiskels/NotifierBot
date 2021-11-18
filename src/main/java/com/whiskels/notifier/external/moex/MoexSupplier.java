package com.whiskels.notifier.external.moex;

import com.whiskels.notifier.external.InMemorySupplier;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MoexSupplier extends InMemorySupplier<MoexRate> implements TelegramLabeled {
    @Value("${external.customer.debt.telegram.label:Currency rates}")
    private String telegramLabel;

    @Scheduled(cron = "${moex.cron:0 0 0 * * *}", zone = "${common.timezone}")
    public void updateScheduled() {
        update();
    }

    @Override
    public String getLabel() {
        return telegramLabel;
    }
}
