package com.whiskels.notifier.telegram.annotations;

import com.whiskels.notifier.model.Role;
import com.whiskels.notifier.model.User;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines array of {@link Role com.whiskels.notifier.model.Role} any of which {@link User com.whiskels.notifier.model.User}
 * should have in order to gain access to the annotated method
 * <p>
 * Used on inheritors of {@link com.whiskels.notifier.telegram.handler.AbstractBaseHandler}
 *
 * @see com.whiskels.notifier.telegram.handler.GetHandler example of annotation implementation
 * @see com.whiskels.notifier.telegram.security.AuthorizationService example of role validation
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface RequiredRoles {
    /**
     * Returns an array of user roles that have access to the annotated method
     *
     * @return an array of user roles that have access to the annotated method
     */
    Role[] roles();
}
