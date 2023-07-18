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

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.isLaterThan;
import static com.whiskels.notifier.common.util.DateTimeUtil.isSameMonth;

@Component
@ConditionalOnProperty("slack.employee.webhook")
@ConditionalOnBean(value = EmployeeDto.class, parameterizedContainer = ReportSupplier.class)
class EmployeeEventReporterAtMonthMiddle extends AbstractEmployeeEventReporter {
    private final String header;

    public EmployeeEventReporterAtMonthMiddle(@Value("${slack.employee.webhook}") String webHook,
                                              @Value("${slack.employee.month-middle.header:Upcoming employee events till the end of the month}") String header,
                                              ReportSupplier<EmployeeDto> provider,
                                              SlackWebHookExecutor executor) {
        super(webHook, provider, executor);
        this.header = header;
    }

    @Scheduled(cron = "${slack.employee.month-middle.cron:0 0 9 15 * *}", zone = "${common.timezone}")
    public void executeScheduled() {
        prepareAndSend();
    }

    public void prepareAndSend() {
        var payload = createPayload(header);
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
                isSameMonth(func, data.getReportDate()),
                isLaterThan(func, data.getReportDate()));
    }
}
