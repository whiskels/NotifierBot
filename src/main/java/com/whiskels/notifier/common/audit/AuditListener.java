package com.whiskels.notifier.common.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
class AuditListener {
    private Clock clock;

    @Autowired
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @PrePersist
    private void prePersist(AbstractAuditedEntity entity) {
        log.debug("Setting load time and date to audited entity {}", entity);
        entity.setLoadDate(LocalDate.now(clock));
        entity.setLoadDateTime(LocalDateTime.now(clock));
    }
}
