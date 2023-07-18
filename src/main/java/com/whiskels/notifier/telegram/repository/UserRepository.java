package com.whiskels.notifier.telegram.repository;

import com.whiskels.notifier.telegram.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> getByChatId(Long chatId);
}
