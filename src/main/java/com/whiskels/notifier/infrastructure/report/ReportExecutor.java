package com.whiskels.notifier.infrastructure.report;

import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.service.Report;

public interface ReportExecutor {
    void send(ReportType type, Report report);
}
