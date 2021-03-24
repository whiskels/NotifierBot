package com.whiskels.notifier.external.receivable.service;

import com.whiskels.notifier.common.ReportBuilder;
import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_NEW_LINE;
import static com.whiskels.notifier.external.receivable.util.ReceivableUtil.CATEGORY_REVENUE;
import static java.time.LocalDate.now;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(value = ReceivableDto.class, parameterizedContainer = DataProvider.class)
public class ReceivableDailyReporter implements DailyReporter<ReceivableDto> {
    private final Clock clock;
    private final DataProvider<ReceivableDto> provider;

    public String dailyReport(Predicate<ReceivableDto> predicate) {
        log.debug("Preparing customer receivable message");
        final LocalDate reportDate = now(clock);

        return ReportBuilder.withHeader(CATEGORY_REVENUE, reportDate)
                .list(provider.get(),
                        predicate, COLLECTOR_NEW_LINE)
                .build();
    }
}
