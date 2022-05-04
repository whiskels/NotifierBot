package com.whiskels.notifier.telegram.service;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DemoUserServiceTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final UserService userService = new UserService(repository);

    @Test
    void getOrCreate() {
        when(repository.getByChatId(1L)).thenReturn(Optional.of(USER_1));

        when(repository.getByChatId(2L)).thenReturn(Optional.empty());

        User expected = new User(2L);
        expected.setRoles(Set.of(ADMIN));
        when(repository.save(any())).thenReturn(expected);

        User actualPresent = userService.getOrCreate(1L);
        verify(repository).getByChatId(1L);
        User actualMissing = userService.getOrCreate(2L);
        verify(repository).getByChatId(2L);
        verify(repository).save(any());
        verifyNoMoreInteractions(repository);

        assertEquals(USER_1, actualPresent);
        assertEquals(expected, actualMissing);
    }
}
