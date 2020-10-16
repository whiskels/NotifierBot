package com.whiskels.telegrambot.annotations;

import com.whiskels.telegrambot.model.Role;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Schedulable {
    Role[] roles();
}

