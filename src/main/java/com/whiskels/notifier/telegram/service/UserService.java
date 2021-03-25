package com.whiskels.notifier.telegram.service;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile({"telegram-common", "!demo"})
public class UserService {
    protected final UserRepository userRepository;

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
