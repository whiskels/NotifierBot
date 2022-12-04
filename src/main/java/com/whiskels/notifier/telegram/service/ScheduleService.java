package com.whiskels.notifier.telegram.service;

import com.whiskels.notifier.telegram.domain.Schedule;
import com.whiskels.notifier.telegram.repository.ScheduleRepository;
import com.whiskels.notifier.telegram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("telegram-common")
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public List<Schedule> findScheduled(LocalTime ldt) {
        return scheduleRepository.getAllByHourAndMinute(ldt.getHour(), ldt.getMinute());
    }

    public int clear(int userId) {
        return scheduleRepository.delete(userId);
    }

    public List<Schedule> getSchedule(Long chatId) {
        return scheduleRepository.getAll(chatId);
    }

    @Transactional
    public Schedule addSchedule(Schedule schedule, int userId) {
        schedule.setUser(userRepository.getById(userId));
        return scheduleRepository.save(schedule);
    }
}
