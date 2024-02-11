package com.whiskels.notifier.reporting.service.audit;

import com.whiskels.notifier.reporting.ReportType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface AuditDataFetchResult {
    ReportType reportType();
}
