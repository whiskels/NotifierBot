package com.whiskels.notifier.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.FetchType.LAZY;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Table(name = "schedule", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "hour", "minutes"}, name = "schedule_unique_user_time_idx")})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Schedule extends AbstractBaseEntity {

    @Column(name = "hour", nullable = false)
    @Range(min = 0, max = 23)
    private int hour;

    @Column(name = "minutes", nullable = false, columnDefinition = "integer default 0")
    @Range(min = 0, max = 59)
    private int minutes;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = CASCADE)
    @NotNull
    private User user;
}
