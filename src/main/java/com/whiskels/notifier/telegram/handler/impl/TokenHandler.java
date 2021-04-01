package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.stream.Collectors;

import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;

/**
 * Sends user his token
 * <p>
 * Available to: everyone
 */
@Slf4j
@BotCommand(command = "/TOKEN", message = "Show your token")
public class TokenHandler extends AbstractBaseHandler {
    public TokenHandler(AuthorizationService authorizationService,
                        ApplicationEventPublisher publisher) {
        super(authorizationService, publisher);
    }

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /TOKEN");
        publish(create(user)
                .line("Your token is *%s*", user.getChatId())
                .line("Your roles are: %s", user.getRoles().stream()
                        .map(Enum::toString)
                        .collect(Collectors.joining(", ")))
                .build());
    }
}
