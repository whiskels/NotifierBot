package com.whiskels.notifier.external.json.operation.repository;

import com.whiskels.notifier.common.audit.repository.AuditRepository;
import com.whiskels.notifier.external.json.operation.domain.FinancialOperation;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@ConditionalOnProperty("external.customer.operation.url")
public interface FinOperationRepository extends AuditRepository<FinancialOperation>,
        JpaSpecificationExecutor<FinancialOperation>, TelegramLabeled {
    @Query("select r.crmId from FinancialOperation r")
    List<Integer> getPresentCrmIdList();
}
