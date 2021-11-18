package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.google.customer.CustomerBirthdayInfo;
import com.whiskels.notifier.slack.reporter.AbstractCustomerEventReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.isSameMonth;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.notNull;

@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.customer.birthday.webhook")
@ConditionalOnBean(value = CustomerBirthdayInfo.class, parameterizedContainer = Supplier.class)
public class CustomerEventReporterAtMonthStart extends AbstractCustomerEventReporter {
    @Value("${slack.customer.birthday.header.monthStart:Upcoming customer birthdays this month}")
    private String header;

    public CustomerEventReporterAtMonthStart(@Value("${slack.customer.birthday.webhook}") String webHook,
                                             Supplier<CustomerBirthdayInfo> provider,
                                             ApplicationEventPublisher publisher) {
         super(webHook, provider, publisher);
    }

    @Scheduled(cron = "${slack.customer.birthday.cron.monthStart:0 0 9 1 * *}", zone = "${common.timezone}")
    public void report() {
        createPayload(header);
    }

    protected List<Predicate<CustomerBirthdayInfo>> birthdayPredicates() {
        return List.of(notNull(CustomerBirthdayInfo::getBirthday),
                isSameMonth(CustomerBirthdayInfo::getBirthday, provider.lastUpdate()));
    }
}
