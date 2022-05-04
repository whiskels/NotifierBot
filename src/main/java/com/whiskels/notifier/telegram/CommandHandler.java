package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.domain.User;

public interface CommandHandler {
    void handle(User user, String message);

    Command getCommand();
}
