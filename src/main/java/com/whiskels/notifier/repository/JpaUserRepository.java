package com.whiskels.notifier.repository;

import com.whiskels.notifier.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface JpaUserRepository extends JpaRepository<User, Integer> {
    Optional<User> getByChatId(int chatId);
}
