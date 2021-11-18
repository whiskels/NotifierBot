package com.whiskels.notifier.external.json.operation.service;

import com.whiskels.notifier.external.DataLoader;
import com.whiskels.notifier.external.audit.repository.LoadAuditRepository;
import com.whiskels.notifier.external.json.operation.domain.FinancialOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.external.audit.domain.LoadAudit.audit;
import static com.whiskels.notifier.external.audit.domain.Loader.FINANCIAL_OPERATION;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(value = FinancialOperation.class, parameterizedContainer = DataLoader.class)
public class FinOperationDataLoaderAuditor {
    private final LoadAuditRepository auditRepository;

    public LocalDate lastUpdate() {
        return auditRepository.getLastUpdateDate(FINANCIAL_OPERATION);
    }

    public void saveLoadAuditResults(List<FinancialOperation> operations) {
        log.info("[{}] LoadAudit results saved: {}"
                , FINANCIAL_OPERATION
                , auditRepository.save(audit(operations, FINANCIAL_OPERATION))
        );
    }
}
