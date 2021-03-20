package com.whiskels.notifier.telegram.repository;

import com.whiskels.notifier.telegram.domain.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
@Profile("telegram-common")
public interface JpaUserRepository extends JpaRepository<User, Integer> {
    Optional<User> getByChatId(int chatId);
}
