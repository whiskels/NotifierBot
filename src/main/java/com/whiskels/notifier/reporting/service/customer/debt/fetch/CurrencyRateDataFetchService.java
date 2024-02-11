package com.whiskels.notifier.reporting.service.customer.debt.fetch;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.audit.AuditDataFetchResult;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CurrencyRate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.reporting.ReportType.CURRENCY_RATE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@RequiredArgsConstructor
public class CurrencyRateDataFetchService implements DataFetchService<CurrencyRate> {
    private final CurrencyRateFeignClient client;
    private final Clock clock;

    @AuditDataFetchResult(reportType = CURRENCY_RATE)
    @NonNull
    @Override
    public ReportData<CurrencyRate> fetch() {
        var data = client.get();
        List<CurrencyRate> wrappedData = data != null ? singletonList(data) : emptyList();

        return new ReportData<>(wrappedData, LocalDate.now(clock));
    }
}
