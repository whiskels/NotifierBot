package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.ReportData;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.debt.DebtDto;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.whiskels.notifier.common.util.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_TWO_NEW_LINES;
import static com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder.builder;

@Component
@ConditionalOnProperty("slack.customer.debt.webhook")
@ConditionalOnBean(value = DebtDto.class, parameterizedContainer = ReportSupplier.class)
class DebtDailyReporter extends SlackReporter<DebtDto> {
    private static final int SINGLE_REPORT_CUTOFF = 20;
    private final String header;

    public DebtDailyReporter(@Value("${slack.customer.debt.webhook}") String webHook,
                             @Value("${slack.customer.debt.header:Debt report on}") String header,
                             ReportSupplier<DebtDto> provider,
                             SlackWebHookExecutor executor) {
        super(webHook, executor, provider);
        this.header = header;
    }

    @Scheduled(cron = "${slack.customer.debt.cron:0 0 13 * * MON-FRI}", zone = "${common.timezone}")
    public void executeScheduled() {
        prepareAndSend();
    }

    public void prepareAndSend() {
        var data = provider.get();
        if (data.getContent().size() < SINGLE_REPORT_CUTOFF) {
            singleReport(data);
        } else {
            multiReport(data);
        }
    }

    private void singleReport(ReportData<DebtDto> data) {
        var report = builder()
                .hook(webHook)
                .collector(COLLECTOR_TWO_NEW_LINES)
                .header(header + reportDate(data.getReportDate()))
                .notifyChannel()
                .block(data.getContent())
                .build();
        executor.execute(report);
    }

    private void multiReport(ReportData<DebtDto> data) {
        List<SlackPayload> reports = new ArrayList<>();
        boolean isFirstReport = true;
        var content = data.getContent();
        int reportNum = 1;
        for (int i = 0; i < content.size(); i += SINGLE_REPORT_CUTOFF) {
            var currentReportBuilder = builder();

            if (isFirstReport) {
                currentReportBuilder.notifyChannel();
                isFirstReport = false;
            }
            currentReportBuilder.hook(webHook)
                    .collector(COLLECTOR_TWO_NEW_LINES)
                    .header(header + reportDate(data.getReportDate()) + " #" + reportNum++)
                    .block(content.subList(i, Math.min(content.size(), i + SINGLE_REPORT_CUTOFF)));

            reports.add(currentReportBuilder.build());
        }

        reports.forEach(executor::execute);
    }
}
