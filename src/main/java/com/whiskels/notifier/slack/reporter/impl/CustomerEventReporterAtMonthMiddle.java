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

import static com.whiskels.notifier.common.util.DateTimeUtil.isLaterThan;
import static com.whiskels.notifier.common.util.DateTimeUtil.isSameMonth;

@Component
@ConditionalOnProperty("slack.customer.birthday.webhook")
@ConditionalOnBean(value = CustomerBirthdayInfoDto.class, parameterizedContainer = ReportSupplier.class)
class CustomerEventReporterAtMonthMiddle extends AbstractCustomerEventReporter {
    private final String header;

    public CustomerEventReporterAtMonthMiddle(@Value("${slack.customer.birthday.webhook}") String webHook,
                                              @Value("${slack.customer.birthday.month-middle.header:Upcoming customer birthdays till the end of the month}") String header,
                                              ReportSupplier<CustomerBirthdayInfoDto> provider,
                                              SlackWebHookExecutor executor) {
        super(webHook, provider, executor);
        this.header = header;
    }

    @Scheduled(cron = "${slack.customer.birthday.month-middle.cron:0 0 9 15 * *}", zone = "${common.timezone}")
    public void executeScheduled() {
        prepareAndSend();
    }

    public void prepareAndSend() {
        var payload = createPayload(header);
        executor.execute(payload);
    }

    protected List<Predicate<CustomerBirthdayInfoDto>> birthdayPredicates() {
        var data = provider.get();
        return List.of(Util.notNull(CustomerBirthdayInfoDto::getBirthday),
                isSameMonth(CustomerBirthdayInfoDto::getBirthday, data.getReportDate()),
                isLaterThan(CustomerBirthdayInfoDto::getBirthday, data.getReportDate()));
    }
}
