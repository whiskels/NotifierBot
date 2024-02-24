package com.whiskels.notifier.reporting;

import javax.annotation.Nonnull;
import java.util.List;

public interface ReportService {
    void executeReport(@Nonnull ReportType type);

    List<ReportType> getReportTypes();
}
