package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.annotation.Schedulable;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.whiskels.notifier.telegram.util.ParsingUtil.extractCommand;

/**
 * Chooses suitable inheritor of AbstractBaseHandler to handle the input
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("telegram-common")
public class HandlerOrchestrator {
    @Getter
    private final List<AbstractBaseHandler> handlers;
    private final UserService userService;

    public void operateCommand(int userId, String text) {
        try {
            AbstractBaseHandler handler = getHandler(text);
            log.debug("Found handler {} for command {}", handler.getClass().getSimpleName(), text);
            handler.authorizeAndHandle(userService.getOrCreate(userId), text);
        } catch (UnsupportedOperationException e) {
            log.error("Command: {} is unsupported", text);
        }
    }

    public void operateSchedule(User user) {
        try {
            AbstractBaseHandler handler = getSchedulableHandler(user.getRoles());
            log.debug("Found scheduled handler {} ", handler.getClass().getSimpleName());
            handler.authorizeAndHandle(user, null);
        } catch (UnsupportedOperationException e) {
            log.error("User: {} called schedulable handler, but nothing was found", user);
        }
    }

    /**
     * Selects handler which can handle received command
     *
     * @param text content of received message
     * @return handler suitable for command
     */
    private AbstractBaseHandler getHandler(String text) {
        return handlers.stream()
                .filter(h -> h.getClass()
                        .isAnnotationPresent(BotCommand.class))
                .filter(h -> Stream.of(h.getClass()
                        .getAnnotation(BotCommand.class)
                        .command())
                        .anyMatch(c -> c.toString().equalsIgnoreCase(extractCommand(text))))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }


    /**
     * Searches for an {@link AbstractBaseHandler} that supports {@link Schedulable} annotation where
     * any of defined roles are presented in the set of {@link User} roles
     * <p>
     * Note: current realization suggests that any user role can schedule no more than one handler
     *
     * @param roles {@link Role} that scheduled an event
     * @return {@link AbstractBaseHandler} that was scheduled by user
     */
    private AbstractBaseHandler getSchedulableHandler(Set<Role> roles) {
        return handlers.stream()
                .filter(h -> h.getClass()
                        .isAnnotationPresent(Schedulable.class))
                .filter(h -> Stream.of(h.getClass()
                        .getAnnotation(Schedulable.class)
                        .roles())
                        .anyMatch(roles::contains))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }
}
