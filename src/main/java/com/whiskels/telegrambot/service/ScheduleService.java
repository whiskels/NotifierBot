package com.whiskels.telegrambot.service;

import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.repository.JpaScheduleRepository;
import com.whiskels.telegrambot.repository.JpaUserRepository;
import org.springframework.stereotype.Service;

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

    public void clearSchedule(String chatId) {
        scheduleRepository.clear(chatId);
    }

    public List<Schedule> getSchedule(String chatId) {
        return scheduleRepository.getAll(chatId);
    }

    public void addSchedule(String chatId, int hours, int minutes) {
        final User user = userRepository.getOneByChatId(chatId);
        scheduleRepository.save(new Schedule(hours, minutes, user));
    }
}
