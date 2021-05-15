package com.whiskels.notifier.telegram.annotation;

import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.impl.DebtHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines array of {@link Role} any of which {@link User}
 * should have in order to schedule designated task
 * <p>
 * Used on inheritors of {@link com.whiskels.notifier.telegram.handler.AbstractBaseHandler}
 *
 * @see DebtHandler example of annotation implementation
 * @see com.whiskels.notifier.telegram.MessageScheduler example of scheduling (handle(User, String) method of
 * AbstractBaseHandler
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Schedulable {
    /**
     * Returns an array of user roles that can schedule execution of the annotated method
     *
     * @return an array of user roles that can schedule execution of the annotated method
     */
    Role[] roles();
}

