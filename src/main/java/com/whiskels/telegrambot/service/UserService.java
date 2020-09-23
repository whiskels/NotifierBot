package com.whiskels.telegrambot.service;

import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.repository.JpaUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.whiskels.telegrambot.util.ValidationUtil.checkNotFoundWithId;

@Service
public class UserService {
    private final JpaUserRepository userRepository;

    public UserService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get(int chatId) {
        final User user = userRepository.getByChatId(chatId).orElse(null);

        return user != null ? user : userRepository.save(new User(chatId));
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
