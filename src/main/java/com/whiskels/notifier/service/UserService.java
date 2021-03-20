package com.whiskels.notifier.service;

import com.whiskels.notifier.model.User;
import com.whiskels.notifier.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("telegram-common")
public class UserService {
    private final JpaUserRepository userRepository;

    public User getOrCreate(int chatId) {
        return get(chatId)
                .orElseGet(() -> userRepository.save(new User(chatId)));
    }

    public Optional<User> get(int chatId) {
        return userRepository.getByChatId(chatId);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
