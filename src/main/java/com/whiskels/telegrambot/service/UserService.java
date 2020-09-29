package com.whiskels.telegrambot.service;

import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.repository.JpaUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final JpaUserRepository userRepository;

    public UserService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreate(int chatId) {
        return userRepository.getByChatId(chatId)
                .orElseGet(() -> userRepository.save(new User(chatId)));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
