package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.common.util.Util;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.google.customer.CustomerBirthdayInfoDto;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.AbstractCustomerEventReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.isSameDay;
import static com.whiskels.notifier.common.util.DateTimeUtil.reportDate;

@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.customer.birthday.webhook")
@ConditionalOnBean(value = CustomerBirthdayInfoDto.class, parameterizedContainer = ReportSupplier.class)
class CustomerEventReporterOnEvent extends AbstractCustomerEventReporter {
    private final String header;

    public CustomerEventReporterOnEvent(@Value("${slack.customer.birthday.webhook}") String webHook,
                                        @Value("${slack.customer.birthday.daily.header:Customer birthdays on}") String header,
                                        ReportSupplier<CustomerBirthdayInfoDto> provider,
                                        SlackWebHookExecutor executor) {
        super(webHook, provider, executor);
        this.header = header;
    }

    @Scheduled(cron = "${slack.customer.birthday.daily.cron:0 0 9 * * *}", zone = "${common.timezone}")
    public void executeScheduled() {
        executor.execute(prepare());
    }

    public SlackPayload prepare() {
        var data = provider.get();
        return createPayload(header + reportDate(data.getReportDate()), true);
    }

    protected List<Predicate<CustomerBirthdayInfoDto>> birthdayPredicates() {
        var data = provider.get();
        return List.of(Util.notNull(CustomerBirthdayInfoDto::getBirthday),
                isSameDay(CustomerBirthdayInfoDto::getBirthday, data.getReportDate()));
    }
}
