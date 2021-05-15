package com.whiskels.notifier.external.audit.repository;

import com.whiskels.notifier.external.audit.domain.LoadAudit;
import com.whiskels.notifier.external.audit.domain.Loader;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Lazy
@Repository
@Transactional(readOnly = true)
public interface LoadAuditRepository extends JpaRepository<LoadAudit, Integer> {
    @Query("select max(la.date) from LoadAudit la where la.loader=:loader")
    LocalDate getLastUpdateDate(@Param("loader") Loader loader);

    @Transactional
    @Modifying
    @Query("delete from LoadAudit la where la.date < :date")
    int deleteByDateBefore(@Param("date") LocalDate date);
}
