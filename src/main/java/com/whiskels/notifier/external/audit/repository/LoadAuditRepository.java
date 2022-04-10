package com.whiskels.notifier.external.audit.repository;

import com.whiskels.notifier.common.audit.repository.AuditRepository;
import com.whiskels.notifier.external.audit.domain.LoadAudit;
import com.whiskels.notifier.external.audit.domain.Loader;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Lazy
@Repository
@Transactional(readOnly = true)
public interface LoadAuditRepository extends AuditRepository<LoadAudit> {
    @Query("select max(la.loadDate) from LoadAudit la where la.loader=:loader")
    LocalDate getLastUpdateDate(@Param("loader") String loader);

    default LocalDate getLastUpdateDate(Loader loader) {
        return getLastUpdateDate(loader.name());
    }
}
