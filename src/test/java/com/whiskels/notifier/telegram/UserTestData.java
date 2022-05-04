package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.domain.User;
import lombok.experimental.UtilityClass;

import java.util.Set;

import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static com.whiskels.notifier.telegram.domain.Role.UNAUTHORIZED;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

@UtilityClass
public class UserTestData {
    public static final User USER_1 = new User(1, 1L, "Test user 1", Set.of(ADMIN), emptyList());
    public static final User USER_2 = new User(2, 2L, "Test user 2", Set.of(UNAUTHORIZED), emptyList());
    public static final User USER_3 = new User(3, 3L, "Test user 3", emptySet(), emptyList());
}
