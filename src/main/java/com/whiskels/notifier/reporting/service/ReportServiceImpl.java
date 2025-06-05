package com.whiskels.notifier.reporting.service;

import com.whiskels.notifier.infrastructure.report.ReportExecutor;
import com.whiskels.notifier.reporting.ReportService;
import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.exception.ExceptionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReportServiceImpl implements ReportService {
    private final Map<ReportType, GenericReportService<?>> processors;
    private final ApplicationEventPublisher publisher;
    private final List<ReportExecutor> reportExecutors;

    public ReportServiceImpl(final Collection<GenericReportService<?>> processors,
                             final List<ReportExecutor> reportExecutors,
                             final ApplicationEventPublisher publisher) {
        this.processors = processors.stream()
                .collect(Collectors.toMap(GenericReportService::getType, Function.identity()));
        this.publisher = publisher;
        this.reportExecutors = reportExecutors;
    }

    @Override
    public void executeReport(@Nonnull final ReportType type) {
        try {
            log.info("[{}] Starting...", type);
            var processor = processors.get(type);

            if (processor == null) {
                throw new IllegalStateException(STR."No processor found for type \{type}");
            }

            for (var report : processor.prepareReports()) {
                reportExecutors.forEach(executor -> executor.send(type, report));
            }

            log.info("[{}] ...Completed", type);
        } catch (Exception e) {
            String exceptionMessage = STR."[\{type}] Exception while processing: \{e.getLocalizedMessage()}";
            publisher.publishEvent(ExceptionEvent.of(exceptionMessage, type));
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ReportType> getReportTypes() {
        return processors.keySet().stream().toList();
    }
}
