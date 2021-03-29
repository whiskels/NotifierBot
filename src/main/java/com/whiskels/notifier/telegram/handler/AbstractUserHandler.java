package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.security.AuthorizationService;
import com.whiskels.notifier.telegram.service.UserService;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Base class for handlers that affect users
 */
public abstract class AbstractUserHandler extends AbstractBaseHandler {
    protected final UserService userService;

    public AbstractUserHandler(AuthorizationService authorizationService,
                               ApplicationEventPublisher publisher,
                               UserService userService) {
        super(authorizationService, publisher);
        this.userService = userService;
    }
}
