package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.google.customer.CustomerBirthdayInfoDto;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.AbstractCustomerEventReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.isSameMonth;
import static com.whiskels.notifier.common.util.Util.notNull;

@Component
@ConditionalOnProperty("slack.customer.birthday.webhook")
@ConditionalOnBean(value = CustomerBirthdayInfoDto.class, parameterizedContainer = ReportSupplier.class)
class CustomerEventReporterAtMonthStart extends AbstractCustomerEventReporter {
    private final String header;

    public CustomerEventReporterAtMonthStart(@Value("${slack.customer.birthday.webhook}") String webHook,
                                             @Value("${slack.customer.birthday.month-start.header:Upcoming customer birthdays this month}") String header,
                                             ReportSupplier<CustomerBirthdayInfoDto> provider,
                                             SlackWebHookExecutor executor) {
        super(webHook, provider, executor);
        this.header = header;
    }

    @Scheduled(cron = "${slack.customer.birthday.month-start.cron:0 0 9 1 * *}", zone = "${common.timezone}")
    public void executeScheduled() {
        executor.execute(prepare());
    }

    public SlackPayload prepare() {
        return createPayload(header);
    }

    protected List<Predicate<CustomerBirthdayInfoDto>> birthdayPredicates() {
        return List.of(notNull(CustomerBirthdayInfoDto::getBirthday),
                isSameMonth(CustomerBirthdayInfoDto::getBirthday, provider.get().getReportDate()));
    }
}
