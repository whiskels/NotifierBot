package com.whiskels.notifier.infrastructure.admin.telegram;

public interface CommandHandler {
    BotMessage handle(final String chatId, final String message);

    Command getCommand();
}
