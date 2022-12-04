package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.domain.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ScheduledCommandHandler extends CommandHandler {
    default SendMessage handleScheduled(User user) {
        return handle(user, null);
    }
}
