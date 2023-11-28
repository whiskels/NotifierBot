package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.common.util.Util;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.google.customer.CustomerBirthdayInfoDto;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.AbstractCustomerEventReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.isSameDay;
import static com.whiskels.notifier.common.util.DateTimeUtil.reportDate;

@Component
@ConditionalOnProperty("slack.customer.birthday.webhook")
@ConditionalOnBean(value = CustomerBirthdayInfoDto.class, parameterizedContainer = ReportSupplier.class)
class CustomerEventReporterBeforeEvent extends AbstractCustomerEventReporter {
    private final String header;
    private final Long daysBefore;


    public CustomerEventReporterBeforeEvent(@Value("${slack.customer.birthday.webhook}") String webHook,
                                            @Value("${slack.customer.birthday.before.header:Customer birthdays in ${slack.customer.birthday.before.days} days from}") String header,
                                            @Value("${slack.customer.birthday.before.days}") Long daysBefore,
                                            ReportSupplier<CustomerBirthdayInfoDto> provider,
                                            SlackWebHookExecutor executor) {
        super(webHook, provider, executor);
        this.header = header;
        this.daysBefore = daysBefore;

    }

    @Scheduled(cron = "${slack.customer.birthday.before.cron:0 0 9 * * *}", zone = "${common.timezone}")
    public void executeScheduled() {
        prepareAndSend();
    }

    public void prepareAndSend() {
        var data = provider.get();
        var payload =  createPayload(header + reportDate(data.getReportDate()), true);
        executor.execute(payload);
    }

    protected List<Predicate<CustomerBirthdayInfoDto>> birthdayPredicates() {
        var data = provider.get();
        return List.of(Util.notNull(CustomerBirthdayInfoDto::getBirthday),
                isSameDay(CustomerBirthdayInfoDto::getBirthday, data.getReportDate().plusDays(daysBefore)));
    }
}
