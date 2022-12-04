package com.whiskels.notifier.external.audit;

import com.whiskels.notifier.common.audit.AuditRepository;
import com.whiskels.notifier.external.LoaderType;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Lazy
@Repository
public interface LoadAuditRepository extends AuditRepository<LoadAudit> {
    @Transactional(readOnly = true)
    @Query("select max(la.loadDate) from LoadAudit la where la.loader=:loader")
    LocalDate getLastUpdateDate(@Param("loader") LoaderType loaderType);
}
