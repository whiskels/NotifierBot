package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * Base class for handlers that affect users
 */
@RequiredArgsConstructor
public abstract class AbstractUserHandler extends AbstractBaseHandler {
    protected final UserService userService;
}
