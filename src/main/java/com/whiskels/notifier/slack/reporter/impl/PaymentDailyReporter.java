package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.operation.PaymentDto;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.whiskels.notifier.common.util.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_NEW_LINE;
import static java.math.BigDecimal.ZERO;

@Slf4j
@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.customer.payment.webhook")
@ConditionalOnBean(value = PaymentDto.class, parameterizedContainer = ReportSupplier.class)
@ConfigurationProperties("slack.customer.payment")
class PaymentDailyReporter extends SlackReporter<PaymentDto> {
    @Value("${slack.customer.payment.header:Payment report on}")
    private String header;

    @Setter
    private Map<Integer, List<String>> pics;

    private final Random rnd = new Random();

    public PaymentDailyReporter(@Value("${slack.customer.payment.webhook}") String webHook,
                                ReportSupplier<PaymentDto> provider,
                                SlackWebHookExecutor executor) {
        super(webHook, executor, provider);
    }

    @Scheduled(cron = "${slack.customer.payment.cron:0 1 13 * * MON-FRI}", zone = "${common.timezone}")
    public void executeScheduled() {
        executor.execute(prepare());
    }

    public SlackPayload prepare() {
        log.debug("Creating employee event payload");
        var data = provider.get();

        return SlackPayloadBuilder.builder()
                .hook(webHook)
                .notifyChannel()
                .header(header + reportDate(data.getReportDate()))
                .collector(COLLECTOR_NEW_LINE)
                .block(data.getContent(), reportPic(data.getContent()))
                .build();
    }

    private String reportPic(List<PaymentDto> data) {
        int cumulativeAmount = data.stream()
                .map(PaymentDto::getAmountRub)
                .reduce(BigDecimal::add)
                .orElse(ZERO).intValue();
        return randomElementFromList(pics.entrySet().stream()
                .min(Comparator.comparingInt(entry -> Math.abs(entry.getKey()) - cumulativeAmount))
                .orElseThrow(() ->
                        new IllegalArgumentException("Exception while trying to find match for condition"))
                .getValue());
    }

    private String randomElementFromList(List<String> list) {
        return list.get(rnd.nextInt(list.size()));
    }
}
