package com.whiskels.notifier.external.json.operation;

import com.whiskels.notifier.external.Loader;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import static com.whiskels.notifier.external.json.operation._FinOperationBeanConfig.PROPERTIES_PREFIX;

@RequiredArgsConstructor
class FinOperationScheduler {
    private final Loader<FinancialOperation> loader;

    @Scheduled(cron = "${" + PROPERTIES_PREFIX + ".cron:0 5 12 * * MON-FRI}", zone = "${common.timezone}")
    public void loadScheduled() {
        loader.load();
    }
}
