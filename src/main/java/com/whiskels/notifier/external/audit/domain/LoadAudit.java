package com.whiskels.notifier.external.audit.domain;

import com.whiskels.notifier.common.audit.domain.AbstractAuditedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static java.lang.String.format;
import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = "load_audit")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class LoadAudit extends AbstractAuditedEntity {
    @Enumerated(STRING)
    @Column(name = "loader", nullable = false)
    @NotBlank
    private Loader loader;

    @Column(name = "count", nullable = false)
    @PositiveOrZero
    @NotNull
    private int count;

    public static <T> LoadAudit audit(List<T> objects, Loader loader) {
        return LoadAudit.builder()
                .count(objects.size())
                .loader(loader)
                .build();
    }

    @Override
    public String toString() {
        return super.toString() + format(" `%s %s`", loader, count);
    }
}
