package com.whiskels.telegrambot.service;

import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.repository.JpaScheduleRepository;
import com.whiskels.telegrambot.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final JpaScheduleRepository scheduleRepository;
    private final JpaUserRepository userRepository;

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
