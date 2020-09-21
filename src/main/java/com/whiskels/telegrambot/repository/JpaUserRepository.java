package com.whiskels.telegrambot.repository;

import com.whiskels.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface JpaUserRepository extends JpaRepository<User, Integer> {
    Optional<User> getByChatId(String chatId);

    @Query("SELECT u FROM User u WHERE u.isAdmin=true")
    List<User> getAdmins();
}
