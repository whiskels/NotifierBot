package com.whiskels.notifier.telegram.security;

import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Checks if user is authorized to use the desired command
 */
@Component
@Slf4j
@Profile("telegram-common")
public class AuthorizationService {
    /**
     * Checks user's permissions if class is annotated with {@link BotCommand}
     *
     * @param user
     * @return authorization result
     */
    public final boolean authorize(Class<?> clazz, User user) {
        try {
            log.debug("Authorizing {} to use {}", user, clazz.getSimpleName());
            final List<Role> requiredRoles = List.of(
                    Stream.of(clazz)
                            .filter(cls -> cls.isAnnotationPresent(BotCommand.class))
                            .findFirst()
                            .orElseThrow(UnsupportedOperationException::new)
                            .getDeclaredAnnotation(BotCommand.class)
                            .requiredRoles());
            log.debug("User roles: {}\nRequired roles: {}", user.getRoles(), requiredRoles);
            if (requiredRoles.contains(Role.UNAUTHORIZED)) {
                return true;
            }
            return !Collections.disjoint(user.getRoles(), requiredRoles);
        } catch (UnsupportedOperationException e) {
            log.error("Attempting to check security on class that is no annotated with @BotCommand: {}",
                    clazz.getSimpleName());
            return true;
        }
    }
}
