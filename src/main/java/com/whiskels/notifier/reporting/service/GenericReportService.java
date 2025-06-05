package com.whiskels.notifier.reporting.service;

import com.whiskels.notifier.reporting.ReportType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenericReportService<I> {
    @Getter
    private final ReportType type;
    private final DataFetchService<I> dataFetchService;
    private final ReportMessageConverter<I> messageCreator;

    public Iterable<Report> prepareReports() {
        var data = dataFetchService.fetch();
        return messageCreator.convert(data);
    }
}
