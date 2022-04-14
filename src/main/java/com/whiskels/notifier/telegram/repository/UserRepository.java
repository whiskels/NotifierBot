package com.whiskels.notifier.telegram.repository;

import com.whiskels.notifier.telegram.domain.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Profile("telegram-common")
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> getByChatId(Long chatId);
}
