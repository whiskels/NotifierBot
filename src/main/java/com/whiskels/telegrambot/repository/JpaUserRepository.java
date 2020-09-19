package com.whiskels.telegrambot.repository;

import com.whiskels.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface JpaUserRepository extends JpaRepository<User, Integer> {
    User getOneByChatId(String chatId);

    List<User> getAllByAdminTrue();

    List<User> getAll();
}
