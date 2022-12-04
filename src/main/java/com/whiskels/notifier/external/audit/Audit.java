package com.whiskels.notifier.external.audit;

import com.whiskels.notifier.external.LoaderType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Audit {
    LoaderType loader();
}
