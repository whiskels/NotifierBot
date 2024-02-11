package com.whiskels.notifier.reporting.service.audit;

import com.whiskels.notifier.infrastructure.repository.AbstractRepository;
import com.whiskels.notifier.reporting.ReportType;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Lazy
@Repository
public interface LoadAuditRepository extends AbstractRepository<LoadAudit> {
    @Transactional(readOnly = true)
    @Query("select max(la.loadDateTime) from LoadAudit la where la.reportType=:reportType")
    Optional<LocalDateTime> findLastUpdateDateTime(@Param("reportType") ReportType reportType);

    @Transactional(readOnly = true)
    @Query("from LoadAudit a order by a.loadDateTime desc")
    List<LoadAudit> getLast(PageRequest pageRequest);
}
