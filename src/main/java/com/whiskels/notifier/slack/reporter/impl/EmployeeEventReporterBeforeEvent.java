package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.common.util.Util;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.employee.EmployeeDto;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.AbstractEmployeeEventReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.isSameDay;
import static com.whiskels.notifier.common.util.DateTimeUtil.reportDate;

@Component
@ConditionalOnProperty("slack.employee.webhook")
@ConditionalOnBean(value = EmployeeDto.class, parameterizedContainer = ReportSupplier.class)
class EmployeeEventReporterBeforeEvent extends AbstractEmployeeEventReporter {
    private final String header;
    private final Long daysBefore;

    public EmployeeEventReporterBeforeEvent(@Value("${slack.employee.webhook}") String webHook,
                                            @Value("${slack.employee.before.header:Employee events in ${slack.employee.before.days} days from}") String header,
                                            @Value("${slack.employee.before.days}") Long daysBefore,
                                            ReportSupplier<EmployeeDto> provider,
                                            SlackWebHookExecutor executor) {
        super(webHook, provider, executor);
        this.header = header;
        this.daysBefore = daysBefore;
    }

    @PostConstruct
    @Scheduled(cron = "${slack.employee.before.cron:0 0 9 * * *}", zone = "${common.timezone}")
    public void sendScheduled() {
        prepareAndSend();
    }

    public void prepareAndSend() {
        var data = provider.get();
        var payload = createPayload(header + reportDate(data.getReportDate()), true);
        executor.execute(payload);
    }

    protected List<Predicate<EmployeeDto>> birthdayPredicates() {
        return generalPredicates(EmployeeDto::getBirthday);
    }

    protected List<Predicate<EmployeeDto>> anniversaryPredicates() {
        return generalPredicates(EmployeeDto::getAppointmentDate);
    }

    private List<Predicate<EmployeeDto>> generalPredicates(Function<EmployeeDto, LocalDate> func) {
        var data = provider.get();
        return List.of(Util.notNull(func),
                isSameDay(func, data.getReportDate().plusDays(daysBefore)));
    }
}
