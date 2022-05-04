package com.whiskels.notifier.telegram.service;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UserServiceTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final UserService userService = new UserService(repository);

    @Test
    void getOrCreate() {
        when(repository.getByChatId(1L)).thenReturn(Optional.of(USER_1));
        User missing = new User(2L);
        when(repository.getByChatId(2L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(missing);

        User actualPresent = userService.getOrCreate(1L);
        verify(repository).getByChatId(1L);
        User actualMissing = userService.getOrCreate(2L);
        verify(repository).getByChatId(2L);
        verify(repository).save(any());
        verifyNoMoreInteractions(repository);

        assertEquals(USER_1, actualPresent);
        assertEquals(missing, actualMissing);
    }

    @Test
    void get() {
        Optional<User> expected = Optional.of(USER_1);
        when(repository.getByChatId(1L)).thenReturn(expected);

        Optional<User> actual = userService.get(1L);

        verify(repository).getByChatId(1L);
        verifyNoMoreInteractions(repository);
        assertEquals(expected, actual);
    }

    @Test
    void update() {
        User expected = new User(1L);
        when(repository.save(expected)).thenReturn(expected);

        User actual = userService.update(expected);

        verify(repository).save(expected);
        verifyNoMoreInteractions(repository);
        assertEquals(expected, actual);
    }

    @Test
    void getUsers() {
        List<User> expected = List.of(USER_1, USER_2);
        when(repository.findAll()).thenReturn(expected);

        List<User> actual = userService.getUsers();

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
        assertEquals(expected, actual);
    }
}
