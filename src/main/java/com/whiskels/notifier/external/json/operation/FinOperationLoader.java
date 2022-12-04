package com.whiskels.notifier.external.json.operation;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.audit.Audit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.LoaderType.FINANCIAL_OPERATION;
import static com.whiskels.notifier.external.json.operation.FinOperationUtil.newCrmId;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;

@Slf4j
@RequiredArgsConstructor
class FinOperationLoader implements Loader<FinancialOperation> {
    private static final Set<DayOfWeek> WEEKEND = Set.of(SATURDAY, SUNDAY);
    private final FinOperationRepository finOperationRepository;
    private final Clock clock;
    private final FinOperationFeignClient finOperationClient;

    @Override
    @Transactional
    @Audit(loader = FINANCIAL_OPERATION)
    public List<FinancialOperation> load() {
        if (isWeekend()) return emptyList();
        var newOperations = getNewFinancialOperations();
        return save(newOperations);
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
}
