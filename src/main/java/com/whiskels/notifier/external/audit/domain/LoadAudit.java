package com.whiskels.notifier.external.audit.domain;

import com.whiskels.notifier.AbstractBaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = "load_audit")
@Builder
@AllArgsConstructor
public class LoadAudit extends AbstractBaseEntity {
    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate date;

    @Enumerated(STRING)
    @Column(name = "loader", nullable = false)
    @NotBlank
    private Loader loader;

    @Column(name = "count", nullable = false)
    @PositiveOrZero
    @NotNull
    private int count;

    @Override
    public String toString() {
        return "LoadAudit{" +
                "id=" + id +
                ", date=" + date +
                ", loader=" + loader +
                ", count=" + count +
                '}';
    }
}
