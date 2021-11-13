package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.DataProvider;
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

import static com.whiskels.notifier.common.datetime.DateTimeUtil.*;

@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.customer.birthday.webhook")
@ConditionalOnBean(value = CustomerBirthdayInfo.class, parameterizedContainer = DataProvider.class)
public class CustomerEventReporterAtMonthMiddle extends AbstractCustomerEventReporter {
    @Value("${slack.customer.birthday.header.monthMiddle:Upcoming customer birthdays till the end of the month}")
    private String header;

    public CustomerEventReporterAtMonthMiddle(@Value("${slack.customer.birthday.webhook}") String webHook,
                                              DataProvider<CustomerBirthdayInfo> provider,
                                              ApplicationEventPublisher publisher) {
        super(webHook, provider, publisher);
    }

    @Scheduled(cron = "${slack.customer.birthday.cron.monthMiddle:0 0 9 15 * *}", zone = "${common.timezone}")
    public void report() {
        createPayload(header);
    }

    protected List<Predicate<CustomerBirthdayInfo>> birthdayPredicates() {
        return List.of(notNull(CustomerBirthdayInfo::getBirthday),
                isSameMonth(CustomerBirthdayInfo::getBirthday, provider.lastUpdate()),
                isLaterThan(CustomerBirthdayInfo::getBirthday, provider.lastUpdate()));
    }
}

