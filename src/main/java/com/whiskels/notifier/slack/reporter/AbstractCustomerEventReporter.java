package com.whiskels.notifier.slack.reporter;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.google.customer.CustomerBirthdayInfoDto;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.birthdayComparator;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.collectToBulletListString;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder.builder;

@Slf4j
public abstract class AbstractCustomerEventReporter extends SlackReporter<CustomerBirthdayInfoDto> {
    @Value("${slack.employee.birthday.noData:Nobody}")
    protected String noData;

    public AbstractCustomerEventReporter(@Value("${slack.customer.birthday.webhook}") String webHook,
                                         ReportSupplier<CustomerBirthdayInfoDto> provider,
                                         SlackWebHookExecutor executor) {
        super(webHook, executor, provider);
    }

    protected abstract List<Predicate<CustomerBirthdayInfoDto>> birthdayPredicates();

    protected final SlackPayload createPayload(String header) {
        return createPayload(header, false);
    }

    protected final SlackPayload createPayload(String header, boolean skipEmpty) {
        log.debug("Creating employee event payload");
        SlackPayloadBuilder builder = builder()
                .hook(webHook)
                .noData(noData)
                .collector(COLLECTOR_COMMA_SEPARATED)
                .header(header)
                .notifyChannel();

        List<CustomerBirthdayInfoDto> birthdays = filterAndSort(provider,
                birthdayComparator(),
                birthdayPredicates());

        if (skipEmpty && birthdays.isEmpty()) {
            log.debug("Customer birthday payload creation aborted: empty event lists and skipEmpty flag is true");
            return null;
        }
        log.debug("Added birthday block to payload: empty = {}, skipEmpty = {}", birthdays.isEmpty(), skipEmpty);
        builder.block(collectToBulletListString(birthdays, CustomerBirthdayInfoDto::toString));

        return builder.build();
    }
}
