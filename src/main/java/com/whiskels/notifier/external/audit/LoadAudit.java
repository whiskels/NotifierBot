package com.whiskels.notifier.external.audit;

import com.whiskels.notifier.common.audit.AbstractAuditedEntity;
import com.whiskels.notifier.external.LoaderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

import static com.whiskels.notifier.common.util.DateTimeUtil.DATE_TIME_FORMATTER;
import static java.lang.String.format;
import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = "load_audit")
@AllArgsConstructor
@NoArgsConstructor
@Builder
class LoadAudit extends AbstractAuditedEntity {
    @Enumerated(STRING)
    @Column(nullable = false)
    @NotBlank
    private LoaderType loader;

    @Column(nullable = false)
    @PositiveOrZero
    @NotNull
    private int count;

    public static LoadAudit loadAudit(int count, LoaderType loaderType) {
        return LoadAudit.builder()
                .count(count)
                .loader(loaderType)
                .build();
    }

    @Override
    public String toString() {
        String time = loadDateTime != null
                ? DATE_TIME_FORMATTER.format(loadDateTime)
                : "undefined";

        return format("%s `%s %s`", time, loader, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LoadAudit loadAudit = (LoadAudit) o;
        return id != null && Objects.equals(id, loadAudit.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
