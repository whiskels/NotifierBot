package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.employee.EmployeeDto;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.AbstractEmployeeEventReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.isSameMonth;
import static com.whiskels.notifier.common.util.Util.notNull;

@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.employee.webhook")
@ConditionalOnBean(value = EmployeeDto.class, parameterizedContainer = ReportSupplier.class)
class EmployeeEventReporterAtMonthStart extends AbstractEmployeeEventReporter {
    private final String header;

    public EmployeeEventReporterAtMonthStart(@Value("${slack.employee.webhook}") String webHook,
                                             @Value("${slack.employee.header.monthStart:Upcoming employee events this month}") String header,
                                             ReportSupplier<EmployeeDto> provider,
                                             SlackWebHookExecutor executor) {
        super(webHook, provider, executor);
        this.header = header;
    }

    @Scheduled(cron = "${slack.employee.cron.monthStart:0 0 9 1 * *}", zone = "${common.timezone}")
    public void executeScheduled() {
        executor.execute(prepare());
    }

    public SlackPayload prepare() {
        return prepare(header);
    }

    protected List<Predicate<EmployeeDto>> birthdayPredicates() {
        return generalPredicates(EmployeeDto::getBirthday);
    }

    protected List<Predicate<EmployeeDto>> anniversaryPredicates() {
        return generalPredicates(EmployeeDto::getAppointmentDate);
    }

    private List<Predicate<EmployeeDto>> generalPredicates(Function<EmployeeDto, LocalDate> func) {
        var data = provider.get();
        return List.of(notNull(func),
                isSameMonth(func, data.getReportDate()));
    }
}
