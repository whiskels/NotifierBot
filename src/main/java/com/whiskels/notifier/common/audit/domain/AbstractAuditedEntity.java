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
}
