package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.domain.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CommandHandler {
    SendMessage handle(User user, String message);

    Command getCommand();
}
