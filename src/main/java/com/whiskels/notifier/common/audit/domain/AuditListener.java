package com.whiskels.notifier.common.audit.domain;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.PrePersist;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class AuditListener {
    private static final Clock clock = Clock.system(ZoneId.of("Europe/Moscow"));

    @PrePersist
    private void prePersist(AbstractAuditedEntity entity) {
        log.debug("Setting load time and date to audited entity {}", entity);
        entity.setLoadDate(LocalDate.now(clock));
        entity.setLoadDateTime(LocalDateTime.now(clock));
    }
}
