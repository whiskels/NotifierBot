package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for handlers that affect users
 */
public abstract class AbstractUserHandler extends AbstractBaseHandler {
    @Autowired
    protected UserService userService;
}
