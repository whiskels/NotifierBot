package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.debt.DebtDto;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.whiskels.notifier.common.util.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_TWO_NEW_LINES;
import static com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder.builder;

@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.customer.debt.webhook")
@ConditionalOnBean(value = DebtDto.class, parameterizedContainer = ReportSupplier.class)
class DebtDailyReporter extends SlackReporter<DebtDto> {
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
        executor.execute(prepare());
    }

    public SlackPayload prepare() {
        var data = provider.get();
        return builder()
                .hook(webHook)
                .collector(COLLECTOR_TWO_NEW_LINES)
                .header(header + reportDate(data.getReportDate()))
                .notifyChannel()
                .block(data.getContent())
                .build();
    }
}
