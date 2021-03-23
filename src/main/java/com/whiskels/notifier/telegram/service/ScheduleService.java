package com.whiskels.notifier.telegram.service;

import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.domain.Schedule;
import com.whiskels.notifier.telegram.repository.ScheduleRepository;
import com.whiskels.notifier.telegram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("telegram-common")
@ConditionalOnBean(annotation = Schedulable.class)
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public List<Schedule> isAnyScheduled(LocalTime ldt) {
        return scheduleRepository.getAllByHourAndMinute(ldt.getHour(), ldt.getMinute());
    }

    public int clear(int userId) {
        return scheduleRepository.delete(userId);
    }

    public List<Schedule> getSchedule(int chatId) {
        return scheduleRepository.getAll(chatId);
    }

    @Transactional
    public Schedule addSchedule(Schedule schedule, int userId) {
        schedule.setUser(userRepository.getOne(userId));
        return scheduleRepository.save(schedule);
    }
}
