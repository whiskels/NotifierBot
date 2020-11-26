package com.whiskels.notifier.telegram.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that a component is a valid handler for bot command
 * <p>
 * Used on inheritors of {@link com.whiskels.notifier.telegram.handler.AbstractBaseHandler}
 *
 * @see com.whiskels.notifier.telegram.handler.CustomerDebtHandler example of implementation
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface BotCommand {
    /**
     * Returns an array of the commands supported by handler
     *
     * @return an array of the commands supported by handler
     */
    String[] command();

    /**
     * Returns help message for the command
     *
     * @return help message for the command
     */
    String message() default "";
}
