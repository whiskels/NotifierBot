package com.whiskels.notifier.telegram.service;

import com.whiskels.notifier.telegram.domain.Schedule;
import com.whiskels.notifier.telegram.repository.ScheduleRepository;
import com.whiskels.notifier.telegram.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static com.whiskels.notifier.telegram.ScheduleTestData.SCHEDULE_USER_1_1;
import static com.whiskels.notifier.telegram.ScheduleTestData.SCHEDULE_USER_1_2;
import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScheduleServiceTest {
    private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ScheduleService scheduleService = new ScheduleService(scheduleRepository, userRepository);

    @Test
    void isAnyScheduled() {
        when(scheduleRepository.getAllByHourAndMinute(10,0)).thenReturn(List.of(SCHEDULE_USER_1_1));

        List<Schedule> actual = scheduleService.isAnyScheduled(LocalTime.of(10,0));

        Assertions.assertThat(actual).containsOnly(SCHEDULE_USER_1_1);
    }

    @Test
    void clear() {
        int expected = 100;
        when(scheduleRepository.delete(1)).thenReturn(expected);

        int actual = scheduleService.clear(1);

        assertEquals(expected, actual);
    }

    @Test
    void getSchedule() {
        when(scheduleRepository.getAll(1L)).thenReturn(List.of(SCHEDULE_USER_1_1, SCHEDULE_USER_1_2));

        List<Schedule> actual = scheduleService.getSchedule(1L);

        Assertions.assertThat(actual).containsExactlyInAnyOrder(SCHEDULE_USER_1_1, SCHEDULE_USER_1_2);
    }

    @Test
    void addSchedule() {
        Schedule input = new Schedule(4, 10, 0, null);
        when(userRepository.getById(1)).thenReturn(USER_1);
        when(scheduleRepository.save(SCHEDULE_USER_1_1)).thenReturn(SCHEDULE_USER_1_1);

        assertEquals(SCHEDULE_USER_1_1, scheduleService.addSchedule(input, 1));
    }
}
