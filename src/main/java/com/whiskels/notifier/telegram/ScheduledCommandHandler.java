package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;

import java.util.Set;

public interface ScheduledCommandHandler extends CommandHandler {
    Set<Role> getRoles();

    default void handleScheduled(User user) {
        handle(user, null);
    }
}
