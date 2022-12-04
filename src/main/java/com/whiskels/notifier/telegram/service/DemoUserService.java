package com.whiskels.notifier.telegram.service;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

@Service
@Profile("demo")
@ConditionalOnMissingBean(UserService.class)
public class DemoUserService extends UserService {
    public DemoUserService(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public User getOrCreate(Long chatId) {
        return get(chatId).orElseGet(() -> {
            User user = new User(chatId);
            user.setRoles(Set.of(ADMIN));
            return userRepository.save(user);
        });
    }
}
