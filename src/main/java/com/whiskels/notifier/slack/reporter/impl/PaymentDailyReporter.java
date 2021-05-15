package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.operation.dto.PaymentDto;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_NEW_LINE;

@Slf4j
@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.customer.payment.webhook")
@ConditionalOnBean(value = PaymentDto.class, parameterizedContainer = DataProvider.class)
@ConfigurationProperties("slack.customer.payment")
public class PaymentDailyReporter extends SlackReporter<PaymentDto> {
    private static final ToDoubleFunction<List<PaymentDto>> RECEIVABLE_SUM = x -> x.stream()
            .collect(Collectors.summarizingDouble(PaymentDto::getAmountRub))
            .getSum();

    @Value("${slack.customer.payment.header:Payment report on}")
    private String header;

    @Setter
    private Map<String, List<String>> pics;

    private final Random rnd = new Random();

    public PaymentDailyReporter(@Value("${slack.customer.payment.webhook}") String webHook,
                                DataProvider<PaymentDto> provider,
                                ApplicationEventPublisher publisher) {
        super(webHook, publisher, provider);
    }

    @Scheduled(cron = "${slack.customer.payment.cron:0 1 13 * * MON-FRI}", zone = "${common.timezone}")
    public void report() {
        log.debug("Creating employee event payload");
        List<PaymentDto> data = provider.get();

        publish(SlackPayloadBuilder.builder()
                .hook(webHook)
                .notifyChannel()
                .header(header + reportDate(provider.lastUpdate()))
                .collector(COLLECTOR_NEW_LINE)
                .block(data, reportPic(data))
                .build());
    }

    private String reportPic(List<PaymentDto> data) {
        if (data.isEmpty()) {
            return randomElementFromList(pics.get(Collections.min(pics.keySet())));
        } else {
            double conditionResult = RECEIVABLE_SUM.applyAsDouble(data);
            return randomElementFromList(pics.entrySet().stream()
                    .min(Comparator.comparingDouble(entry -> Math.abs(Double.parseDouble(entry.getKey()) - conditionResult)))
                    .orElseThrow(() ->
                            new IllegalArgumentException("Exception while trying to find match for condition " + RECEIVABLE_SUM))
                    .getValue());
        }
    }

    private String randomElementFromList(List<String> list) {
        return list.get(rnd.nextInt(list.size()));
    }
}
