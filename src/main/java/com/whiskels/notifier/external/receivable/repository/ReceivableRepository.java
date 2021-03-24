package com.whiskels.notifier.external.receivable.repository;

import com.whiskels.notifier.external.receivable.domain.Receivable;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface ReceivableRepository extends JpaRepository<Receivable, Integer> {
    @Query("select new com.whiskels.notifier.external.receivable.dto.ReceivableDto(r.currency, r.amount, r.contractor) " +
            "from Receivable r where r.loadDate = :date and r.category='Revenue' order by r.amountRub desc ")
    List<ReceivableDto> getRevenueByDate(@Param("date") LocalDate date);

    @Transactional
    @Modifying
    @Query("delete from Receivable r where r.loadDate < :date")
    int deleteByDateBefore(@Param("date") LocalDate date);

    @Query("select r.crmId from Receivable r")
    List<Integer> getIdList();

}
