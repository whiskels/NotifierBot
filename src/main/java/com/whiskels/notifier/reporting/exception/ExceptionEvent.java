package com.whiskels.notifier.reporting.exception;

import com.whiskels.notifier.reporting.ReportType;

public record ExceptionEvent(String message, ReportType type) {
    public static ExceptionEvent of(String message, ReportType type) {
        return new ExceptionEvent(message, type);
    }
}
