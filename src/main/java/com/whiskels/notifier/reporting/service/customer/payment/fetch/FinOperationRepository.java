package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.whiskels.notifier.infrastructure.repository.AbstractRepository;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.whiskels.notifier.reporting.service.customer.payment.config.CustomerPaymentFetchConfig.PAYMENT_URL;


@Repository
@ConditionalOnProperty(PAYMENT_URL)
public interface FinOperationRepository extends AbstractRepository<FinancialOperation> {
    @Query("select r.crmId from FinancialOperation r")
    Set<Integer> getPresentCrmIds();

    @Query("select r from FinancialOperation r" +
            " where r.loadDateTime between :startDate and :endDate" +
            " and r.category = :category" +
            " order by r.amountRub desc")
    List<FinancialOperation> getAllByCategoryAndDateBetween(
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
