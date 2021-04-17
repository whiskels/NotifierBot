package com.whiskels.notifier.external.operation.repository;

import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.operation.dto.FinancialOperationDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
@ConditionalOnProperty("external.customer.receivable.url")
public interface FinOperationRepository extends JpaRepository<FinancialOperation, Integer>,
        JpaSpecificationExecutor<FinancialOperation> {
    @Transactional
    @Modifying
    @Query("delete from FinancialOperation r where r.loadDate < :date")
    int deleteByDateBefore(@Param("date") LocalDate date);

    @Query("select r.crmId from FinancialOperation r")
    List<Integer> getPresentCrmIdList();

    @Query(value = "select max(load_date) from financial_operation", nativeQuery = true)
    LocalDate lastUpdateDate();

    @Query("select new com.whiskels.notifier.external.operation.dto.FinancialOperationDto(" +
            "r.legalName" +
            ", r.contractor" +
            ", r.subcategory" +
            ", sum(r.amountUsd)) " +
            "from  FinancialOperation as r where r.loadDate = :date " +
            "group by r.legalName, r.contractor, r.subcategory, r.amountUsd " +
            "order by abs(r.amountUsd) desc")
    List<FinancialOperationDto> getFinancialOperationsByDate(@Param("date") LocalDate date);
}
