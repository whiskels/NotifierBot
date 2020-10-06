package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.service.UserService;

/**
 * Base class for handlers that affect users
 */
public abstract class AbstractUserHandler extends AbstractBaseHandler {
    protected final UserService userService;

    public AbstractUserHandler(UserService userService) {
        this.userService = userService;
    }
}
