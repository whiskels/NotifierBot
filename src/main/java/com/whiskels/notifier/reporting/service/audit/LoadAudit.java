package com.whiskels.notifier.reporting.service.audit;

import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.domain.AbstractTimeStampedEntity;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

import static java.lang.String.format;
import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = "load_audit")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LoadAudit extends AbstractTimeStampedEntity {
    @Enumerated(STRING)
    @Column(nullable = false)
    @NotBlank
    private ReportType reportType;

    @Column(nullable = false)
    @PositiveOrZero
    private int count;

    public static LoadAudit loadAudit(int count, ReportType reportType) {
        return LoadAudit.builder()
                .count(count)
                .reportType(reportType)
                .build();
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
