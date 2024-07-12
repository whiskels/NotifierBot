package com.whiskels.notifier.reporting.domain;

import lombok.Builder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasBirthdayTest {

    @Test
    void testComparator() {
        var first = "FIRST";
        var second = "SECOND";
        var third = "THIRD";
        var fourth = "FOURTH";

        List<HasBirthday> input = new ArrayList<>();
        input.add(
                HasBirthdayImpl.builder()
                        .name(fourth)
                        .birthday(null)
                        .build()
        );
        input.add(
                HasBirthdayImpl.builder()
                        .name(null)
                        .birthday(null)
                        .build()
        );
        input.add(
                HasBirthdayImpl.builder()
                        .name(first)
                        .birthday(LocalDate.now())
                        .build()
        );
        input.add(
                HasBirthdayImpl.builder()
                        .name(second)
                        .birthday(LocalDate.now().plusDays(1))
                        .build()
        );
        input.add(
                HasBirthdayImpl.builder()
                        .name(third)
                        .birthday(LocalDate.now().plusDays(1))
                        .build()
        );

        input.sort(HasBirthday.comparator());

        assertThat(input).extracting(HasBirthday::name)
                .containsExactly(first, second, third, fourth, null);
    }

    @Builder
    record HasBirthdayImpl(String name, LocalDate birthday) implements HasBirthday {
    }
}