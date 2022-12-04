package com.whiskels.notifier.common.audit;

import com.whiskels.notifier.common.AbstractBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AbstractAuditedEntity that = (AbstractAuditedEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
