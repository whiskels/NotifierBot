package com.whiskels.notifier.reporting.service;

import com.whiskels.notifier.reporting.ReportService;
import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.SlackPayloadExecutor;
import com.whiskels.notifier.reporting.exception.ExceptionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.StringTemplate.STR;

@Slf4j
@Component
public class ReportServiceImpl implements ReportService {
    private final Map<ReportType, GenericReportService<?>> processors;
    private final ApplicationEventPublisher publisher;
    private final SlackPayloadExecutor payloadExecutor;

    public ReportServiceImpl(final Collection<GenericReportService<?>> processors,
                             final SlackPayloadExecutor payloadExecutor,
                             final ApplicationEventPublisher publisher) {
        this.processors = processors.stream()
                .collect(Collectors.toMap(GenericReportService::getType, Function.identity()));
        this.publisher = publisher;
        this.payloadExecutor = payloadExecutor;
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
                payloadExecutor.send(type, report);
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
