package com.whiskels.notifier.external.operation.service;

import com.whiskels.notifier.external.DataLoader;
import com.whiskels.notifier.external.audit.domain.LoadAudit;
import com.whiskels.notifier.external.audit.domain.Loader;
import com.whiskels.notifier.external.audit.repository.LoadAuditRepository;
import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.external.audit.domain.Loader.FINANCIAL_OPERATION;
import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(value = FinancialOperation.class, parameterizedContainer = DataLoader.class)
public class FinOperationDataLoaderAuditor {
    private static final Loader LOADER = FINANCIAL_OPERATION;

    private final LoadAuditRepository auditRepository;
    private final Clock clock;

    public LocalDate lastUpdate() {
        return auditRepository.getLastUpdateDate(FINANCIAL_OPERATION);
    }

    public void saveLoadAuditResults(List<FinancialOperation> operations) {
        log.info("LoadAudit results saved: {}", auditRepository.save(audit(operations)));
    }

    private LoadAudit audit(List<FinancialOperation> operations) {
        return LoadAudit.builder()
                .count(operations.size())
                .date(now(clock))
                .loader(LOADER)
                .build();
    }

}
