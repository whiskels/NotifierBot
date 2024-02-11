package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.audit.AuditDataFetchResult;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_PAYMENT;
import static com.whiskels.notifier.utilities.collections.StreamUtil.filterAndSort;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;

@Slf4j
@RequiredArgsConstructor
public class FinOperationDataFetchService implements DataFetchService<FinancialOperation> {
    private static final Set<DayOfWeek> WEEKEND = Set.of(SATURDAY, SUNDAY);
    private static final Collector<CharSequence, ?, String> COLLECTOR_COMMA_SEPARATED = Collectors.joining(", ");

    private final FinOperationRepository finOperationRepository;
    private final Clock clock;
    private final FinOperationFeignClient finOperationClient;

    @Nonnull
    @Transactional
    @AuditDataFetchResult(reportType = CUSTOMER_PAYMENT)
    public ReportData<FinancialOperation> fetch() {
        if (isWeekend()) return new ReportData<>(emptyList(), now(clock));
        var newOperations = getNewFinancialOperations();
        return new ReportData<>(save(newOperations), now(clock));
    }

    private boolean isWeekend() {
        DayOfWeek today = now(clock).getDayOfWeek();
        return WEEKEND.contains(today);
    }

    private List<FinancialOperation> getNewFinancialOperations() {
        log.info("Preparing to check for new financial operations");
        Set<Integer> presentIds = finOperationRepository.getPresentCrmIds();

        List<FinancialOperation> newOperations = filterAndSort(finOperationClient.get(), newCrmId(presentIds));

        log.info("Found {} new financial operations with crm ids: {}",
                newOperations.size(),
                newOperations.stream()
                        .map(FinancialOperation::getCrmId)
                        .map(String::valueOf)
                        .collect(COLLECTOR_COMMA_SEPARATED)
        );

        return newOperations;
    }

    private List<FinancialOperation> save(List<FinancialOperation> operations) {
        List<FinancialOperation> savedOperations = finOperationRepository.saveAll(operations);
        log.info("Saved {} financial operations", savedOperations);
        return savedOperations;
    }

    private static Predicate<FinancialOperation> newCrmId(Set<Integer> ids) {
        return c -> !ids.contains(c.getCrmId());
    }
}
