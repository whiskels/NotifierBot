package com.whiskels.notifier.slack.reporter;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.employee.EmployeeDto;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.birthdayComparator;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.collectToBulletListString;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.EMPLOYEE_ANNIVERSARY_COMPARATOR;
import static com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder.builder;

@Slf4j
public abstract class AbstractEmployeeEventReporter extends SlackReporter<EmployeeDto> {
    @Value("${slack.employee.event.birthday:*Birthdays:*}")
    protected String birthday;

    @Value("${slack.employee.event.anniversary:*Work anniversaries:*}")
    protected String anniversary;

    @Value("${slack.employee.birthday.noData:Nobody}")
    protected String noData;

    public AbstractEmployeeEventReporter(@Value("${slack.employee.webhook}") String webHook,
                                         ReportSupplier<EmployeeDto> provider,
                                         SlackWebHookExecutor executor) {
        super(webHook, executor, provider);
    }

    protected abstract List<Predicate<EmployeeDto>> anniversaryPredicates();

    protected abstract List<Predicate<EmployeeDto>> birthdayPredicates();

    protected final SlackPayload prepare(String header) {
        return prepare(header, false);
    }

    protected final SlackPayload prepare(String header, boolean skipEmpty) {
        log.debug("Creating employee event payload");
        SlackPayloadBuilder builder = builder()
                .hook(webHook)
                .noData(noData)
                .collector(COLLECTOR_COMMA_SEPARATED)
                .header(header)
                .notifyChannel();

        List<EmployeeDto> birthdays = filterAndSort(provider,
                birthdayComparator(),
                birthdayPredicates());
        List<EmployeeDto> anniversaries = filterAndSort(provider,
                EMPLOYEE_ANNIVERSARY_COMPARATOR,
                anniversaryPredicates());

        if (skipEmpty && birthdays.isEmpty() && anniversaries.isEmpty()) {
            log.debug("Employee payload creation aborted: empty event lists and skipEmpty flag is true");
            return null;
        }

        if (!birthdays.isEmpty() || !skipEmpty) {
            log.debug("Added birthday block to payload: empty = {}, skipEmpty = {}", birthdays.isEmpty(), skipEmpty);
            builder.block(birthday)
                    .block(collectToBulletListString(birthdays, EmployeeDto::toBirthdayString));
        }

        if (!anniversaries.isEmpty() || !skipEmpty) {
            log.debug("Added anniversary block to payload: empty = {}, skipEmpty = {}", anniversaries.isEmpty(), skipEmpty);
            builder.block(anniversary)
                    .block(collectToBulletListString(anniversaries, EmployeeDto::toWorkAnniversaryString));
        }

        return builder.build();
    }
}
