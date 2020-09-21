package com.whiskels.telegrambot.service;

import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.repository.JpaUserRepository;
import com.whiskels.telegrambot.util.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.whiskels.telegrambot.util.ValidationUtil.checkNotFoundWithId;

@Service
public class UserService {
    private JpaUserRepository userRepository;

    public UserService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get(String chatId) {
        Assert.notNull(chatId, "chatId must not be null");
        return checkNotFoundWithId(userRepository.getByChatId(chatId).orElse(null), chatId);
    }

    public User save(User user) {
        Assert.notNull(user, "user must not be null");
        return userRepository.save(user);
    }

    public List<User> getAdmins() {
        return userRepository.getAdmins();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
