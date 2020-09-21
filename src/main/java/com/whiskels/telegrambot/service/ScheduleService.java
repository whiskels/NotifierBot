package com.whiskels.telegrambot.service;

import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.repository.JpaScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService {
    private JpaScheduleRepository scheduleRepository;
    private UserService userService;

    public ScheduleService(JpaScheduleRepository scheduleRepository, UserService userService) {
        this.scheduleRepository = scheduleRepository;
        this.userService = userService;
    }

    public List<Schedule> isAnyScheduled(LocalTime ldt) {
        return scheduleRepository.getAllByHourAndMinute(ldt.getHour(), ldt.getMinute());
    }

    public void clearSchedule(String chatId) {
        scheduleRepository.clear(chatId);
    }

    public List<Schedule> getSchedule(String chatId) {
        return scheduleRepository.getAll(chatId);
    }

    public void addSchedule(String chatId, int hours, int minutes) {
        final User user = userService.get(chatId);
        scheduleRepository.save(new Schedule(hours, minutes, user));
    }
}
