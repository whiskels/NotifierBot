package com.whiskels.notifier.common.audit.domain;

import com.whiskels.notifier.common.AbstractBaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.DATE_TIME_FORMATTER;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
@NoArgsConstructor
public abstract class AbstractAuditedEntity extends AbstractBaseEntity {
    @Column(nullable = false)
    @NotNull
    protected LocalDate loadDate;

    @Column(nullable = false)
    @NotNull
    protected LocalDateTime loadDateTime;

    @Override
    public String toString() {
        return loadDateTime != null
                ? DATE_TIME_FORMATTER.format(loadDateTime)
                : "undefined";
    }
}
