package com.whiskels.telegrambot.security;

import com.whiskels.telegrambot.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

/**
 * Checks if user is authorized to use the desired command
 */
@Component
@Slf4j
public class AuthorizationService {
    /**
     * Checks user's permissions if class has method annotated with {@link RequiredRoles}
     *
     * @param user
     * @return authorization result
     */
    @SneakyThrows
    public final boolean authorize(User user) {
        try {
            // Getting caller class
            // https://stackoverflow.com/a/51768706
            final RequiredRoles annotation = Stream.of(StackWalker.getInstance(RETAIN_CLASS_REFERENCE)
                    .getCallerClass()
                    .getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RequiredRoles.class))
                    .findFirst()
                    .orElseThrow(NoSuchMethodException::new)
                    .getDeclaredAnnotation(RequiredRoles.class);
            return !Collections.disjoint(user.getRoles(), Arrays.asList(annotation.roles()));
        } catch (NoSuchMethodException e) {
            return true;
        }
    }
}
