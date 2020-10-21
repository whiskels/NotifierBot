package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * Base class for handlers that affect users
 */
@RequiredArgsConstructor
public abstract class AbstractUserHandler extends AbstractBaseHandler {
    protected final UserService userService;
}
