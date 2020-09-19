package com.whiskels.telegrambot.service;

import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.repository.JpaUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private JpaUserRepository userRepository;

    public UserService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean containsUser(String chatId) {
        return getUser(chatId) != null;
    }

    public User getUser(String chatId) {
        return userRepository.getOneByChatId(chatId);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAdmins() {
        return userRepository.getAllByAdminTrue();
    }

    public List<User> getUsers() {
        return userRepository.getAll();
    }
}
