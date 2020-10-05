package com.whiskels.telegrambot.service;

import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.repository.JpaScheduleRepository;
import com.whiskels.telegrambot.repository.JpaUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService {
    private JpaScheduleRepository scheduleRepository;
    private JpaUserRepository userRepository;

    public ScheduleService(JpaScheduleRepository scheduleRepository, JpaUserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

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
