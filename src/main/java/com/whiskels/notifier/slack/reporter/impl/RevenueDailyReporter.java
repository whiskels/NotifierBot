package com.whiskels.notifier.slack.reporter.impl;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_NEW_LINE;
import static java.time.LocalDate.now;
import static java.util.Map.entry;

@Component
@Profile("slack-common")
@Slf4j
@ConditionalOnProperty("slack.customer.payment.webhook")
@ConditionalOnBean(value = ReceivableDto.class, parameterizedContainer = DataProvider.class)
public class RevenueDailyReporter extends SlackReporter<ReceivableDto> {
    private static final String NAME = "Receivable";
    private static final String GOOD_PIC_URL = "https://i.imgur.com/bDiTy7t.jpeg";
    private static final String GREAT_PIC_URL = "https://i.imgur.com/AD3MbBi.jpeg";
    private static final Map<Double, String> FUNNY_PICS = Map.ofEntries(
            entry(0d, SAD_PIC_URL),
            entry(100000d, GOOD_PIC_URL),
            entry(1000000d, GREAT_PIC_URL)
    );
    private static final Function<List<ReceivableDto>, Double> RECEIVABLE_SUM = x -> x.stream()
            .collect(Collectors.summarizingDouble(ReceivableDto::getAmountRub))
            .getSum();


    public RevenueDailyReporter(@Value("${slack.customer.payment.webhook}") String webHook,
                                DataProvider<ReceivableDto> provider,
                                ApplicationEventPublisher publisher,
                                Clock clock) {
        super(webHook, provider, clock, publisher);
    }

    @Scheduled(cron = "${slack.customer.payment.cron}", zone = "${common.timezone}")
    protected void report() {
        Payload payload = Payload.builder()
                .text(String.format(REPORT_HEADER, NAME, now(clock)))
                .blocks(List.of(headerBlock(NAME), contentBlock(COLLECTOR_NEW_LINE, RECEIVABLE_SUM, FUNNY_PICS)))
                .build();

        publish(new SlackPayload(webHook, payload));
    }
}
