package com.whiskels.notifier.reporting.domain;

import javax.persistence.PrePersist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Component
public class AbstractTimeStampedEntityListener {

    private Clock clock;

    @Autowired //JPA spec requires @PrePersist classes to have no-args constructor
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @PrePersist
    void prePersist(AbstractTimeStampedEntity entity) {
        log.debug("Setting load time and date to timestamped entity {}", entity);
        entity.setLoadDateTime(LocalDateTime.now(clock));
    }
}
