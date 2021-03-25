package com.whiskels.notifier.external.debt.service;

import com.whiskels.notifier.common.ReportBuilder;
import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.debt.domain.Debt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_EMPTY_LINE;
import static java.time.LocalDate.now;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(value = Debt.class, parameterizedContainer = DataProvider.class)
public class CustomerDebtDailyReporter implements DailyReporter<Debt> {
    private static final String DEBT_REPORT_HEADER = "Overdue debts";

    private final Clock clock;
    private final DataProvider<Debt> provider;

    public String dailyReport(Predicate<Debt> predicate) {
        log.debug("Preparing customer debts message");

        return ReportBuilder.withHeader(DEBT_REPORT_HEADER, now(clock))
                .list(provider.get(), predicate, COLLECTOR_EMPTY_LINE)
                .build();
    }
}
