package com.whiskels.notifier.reporting.service.customer.payment.messaging;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.infrastructure.slack.builder.SlackPayloadBuilder;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import com.whiskels.notifier.reporting.service.customer.payment.domain.CustomerPaymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.whiskels.notifier.utilities.DateTimeUtil.reportDate;
import static com.whiskels.notifier.utilities.formatters.StringFormatter.COLLECTOR_NEW_LINE;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.singleton;

@Slf4j
@RequiredArgsConstructor
public class PaymentReportMessageConverter implements ReportMessageConverter<CustomerPaymentDto> {
    public static final Comparator<CustomerPaymentDto> AMOUNT_COMPARATOR = Comparator.comparing(CustomerPaymentDto::getAmountRub)
            .thenComparing(CustomerPaymentDto::getContractor).reversed();
    private static final Random RANDOM = new Random();

    private final String header;
    private final String noData;
    private final Map<Integer, List<String>> pics;

    @Nonnull
    @Override
    public Iterable<Payload> convert(@Nonnull ReportData<CustomerPaymentDto> reportData) {
        var data = reportData.data();
        var payload =  SlackPayloadBuilder.builder()
                .header(header + reportDate(reportData.requestDate()))
                .notifyChannel()
                .block(mapToReportText(data), reportPic(data))
                .build();
        return singleton(payload);
    }

    private String mapToReportText(List<CustomerPaymentDto> data) {
        if (data.isEmpty()) return noData;
        return data.stream()
                .sorted(AMOUNT_COMPARATOR)
                .map(CustomerPaymentDto::toString)
                .collect(COLLECTOR_NEW_LINE);
    }

    private String reportPic(List<CustomerPaymentDto> data) {
        int cumulativeAmount = data.stream()
                .map(CustomerPaymentDto::getAmountRub)
                .reduce(BigDecimal::add)
                .orElse(ZERO).intValue();
        var picCandidates = pics.entrySet().stream()
                .min(Comparator.comparingInt(entry -> Math.abs(entry.getKey() - cumulativeAmount)))
                .orElseThrow(() ->
                        new IllegalArgumentException("Exception while trying to find match for condition"))
                .getValue();
        return randomElementFromList(picCandidates);
    }

    private String randomElementFromList(List<String> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }
}
