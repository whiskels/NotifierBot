package com.whiskels.notifier.external.json.operation;

import com.whiskels.notifier.common.audit.AuditRepository;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.whiskels.notifier.external.json.operation._FinOperationBeanConfig.FIN_OPERATION_URL;

@Repository
@Transactional(readOnly = true)
@ConditionalOnProperty(FIN_OPERATION_URL)
interface FinOperationRepository extends AuditRepository<FinancialOperation>,
        JpaSpecificationExecutor<FinancialOperation>, TelegramLabeled {
    @Query("select r.crmId from FinancialOperation r")
    Set<Integer> getPresentCrmIds();
}
