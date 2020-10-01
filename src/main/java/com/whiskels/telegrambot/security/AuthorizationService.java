package com.whiskels.telegrambot.security;

import com.whiskels.telegrambot.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

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
    public final boolean authorize(Class clazz, User user) {
        try {
            final RequiredRoles annotation = Stream.of(clazz.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RequiredRoles.class))
                    .findFirst()
                    .orElseThrow(NoSuchMethodException::new)
                    .getDeclaredAnnotation(RequiredRoles.class);
            return !Collections.disjoint(user.getRoles(), Arrays.asList(annotation.roles()));
        } catch (NoSuchMethodException e) {
            log.info("No secured methods in class {}", clazz.getSimpleName());
            return true;
        }
    }
}
