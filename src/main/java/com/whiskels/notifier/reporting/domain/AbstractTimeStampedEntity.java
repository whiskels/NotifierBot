package com.whiskels.notifier.reporting.domain;

import com.whiskels.notifier.infrastructure.domain.AbstractBaseEntity;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AbstractTimeStampedEntityListener.class)
@NoArgsConstructor
public abstract class AbstractTimeStampedEntity extends AbstractBaseEntity {
    @Column(nullable = false)
    @NotNull
    protected LocalDateTime loadDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AbstractTimeStampedEntity that = (AbstractTimeStampedEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
