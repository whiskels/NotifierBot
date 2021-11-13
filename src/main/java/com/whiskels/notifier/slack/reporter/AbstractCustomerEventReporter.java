package com.whiskels.notifier.slack.reporter;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.google.customer.CustomerBirthdayInfo;
import com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.birthdayComparator;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.collectToBulletListString;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder.builder;

@Slf4j
public abstract class AbstractCustomerEventReporter extends SlackReporter<CustomerBirthdayInfo> {
    @Value("${slack.employee.birthday.noData:Nobody}")
    protected String noData;

    public AbstractCustomerEventReporter(@Value("${slack.customer.birthday.webhook}") String webHook,
                                         DataProvider<CustomerBirthdayInfo> provider,
                                         ApplicationEventPublisher publisher) {
        super(webHook, publisher, provider);
    }

    protected abstract List<Predicate<CustomerBirthdayInfo>> birthdayPredicates();

    protected final void createPayload(String header) {
        createPayload(header, false);
    }

    protected final void createPayload(String header, boolean skipEmpty) {
        log.debug("Creating employee event payload");
        SlackPayloadBuilder builder = builder()
                .hook(webHook)
                .noData(noData)
                .collector(COLLECTOR_COMMA_SEPARATED)
                .header(header)
                .notifyChannel();

        List<CustomerBirthdayInfo> birthdays = filterAndSort(provider.getData(),
                birthdayComparator(),
                birthdayPredicates());

        if (skipEmpty && birthdays.isEmpty()) {
            log.debug("Customer birthday payload creation aborted: empty event lists and skipEmpty flag is true");
            return;
        }
            log.debug("Added birthday block to payload: empty = {}, skipEmpty = {}", birthdays.isEmpty(), skipEmpty);
            builder.block(collectToBulletListString(birthdays, CustomerBirthdayInfo::toString));

        publish(builder.build());
    }
}
