package com.whiskels.notifier.telegram.repository;

import com.whiskels.notifier.AbstractRepositoryTest;
import com.whiskels.notifier.telegram.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.List;
import java.util.Optional;

import static com.whiskels.notifier.telegram.UserTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql(scripts = {"classpath:db/clear.sql", "classpath:db/add_users.sql"}, config = @SqlConfig(encoding = "UTF-8"))
    void testGetByChatId() {
        //when
        Optional<User> actual = userRepository.getByChatId(1);

        //then
        assertTrue(actual.isPresent());
        assertEquals(USER_1, actual.get());
    }

    @Test
    @Sql(scripts = {"classpath:db/clear.sql", "classpath:db/add_users.sql"}, config = @SqlConfig(encoding = "UTF-8"))
    void testFindAllUsers() {
        //when
        List<User> actual = userRepository.findAll();

        //then
        assertEquals(3, actual.size());
        assertTrue(actual.containsAll(List.of(USER_1, USER_2, USER_3)));
    }

    @Test
    void testSaveUser() {
        //given
        User expected  = new User(-100);

        //when
        userRepository.save(expected);

        //then
        Optional<User> actual = userRepository.getByChatId(expected.getChatId());
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    @Sql(scripts = {"classpath:db/clear.sql", "classpath:db/add_users.sql"}, config = @SqlConfig(encoding = "UTF-8"))
    void testUpdateUser() {
        //given
        User expected = new User(1, 1, "Updated name", null, null);

        //when
        userRepository.save(expected);
        Optional<User> actual = userRepository.getByChatId(expected.getChatId());

        //then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        assertEquals(expected.getName(), actual.get().getName());
    }
}
