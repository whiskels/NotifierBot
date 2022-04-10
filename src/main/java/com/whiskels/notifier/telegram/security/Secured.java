package com.whiskels.notifier.telegram.security;

import com.whiskels.notifier.telegram.domain.Role;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.whiskels.notifier.telegram.domain.Role.UNAUTHORIZED;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Secured {
    /**
     * Returns an array of user roles that have access to the handler
     * Default: {@link Role#UNAUTHORIZED} - every user can call this handler
     *
     * @return an array of user roles that have access to the handler
     */
    Role[] value() default UNAUTHORIZED;
}
