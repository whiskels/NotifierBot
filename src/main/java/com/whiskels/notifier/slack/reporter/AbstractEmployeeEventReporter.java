package com.whiskels.notifier.slack.reporter;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.json.employee.Employee;
import com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.birthdayComparator;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.collectToBulletListString;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.EMPLOYEE_ANNIVERSARY_COMPARATOR;
import static com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder.builder;

@Slf4j
public abstract class AbstractEmployeeEventReporter extends SlackReporter<Employee> {
    @Value("${slack.employee.event.birthday:*Birthdays:*}")
    protected String birthday;

    @Value("${slack.employee.event.anniversary:*Work anniversaries:*}")
    protected String anniversary;

    @Value("${slack.employee.birthday.noData:Nobody}")
    protected String noData;

    public AbstractEmployeeEventReporter(@Value("${slack.employee.webhook}") String webHook,
                                         DataProvider<Employee> provider,
                                         ApplicationEventPublisher publisher) {
        super(webHook, publisher, provider);
    }

    protected abstract List<Predicate<Employee>> anniversaryPredicates();

    protected abstract List<Predicate<Employee>> birthdayPredicates();

    protected final void createPayload(String header) {
        createPayload(header, false);
    }

    protected final void createPayload(String header, boolean skipEmpty) {
        log.debug("Creating employee event payload");
        SlackPayloadBuilder builder = builder()
                .hook(webHook)
                .noData(noData)
                .collector(COLLECTOR_COMMA_SEPARATED)
                .header(header)
                .notifyChannel();

        List<Employee> birthdays = filterAndSort(provider.getData(),
                birthdayComparator(),
                birthdayPredicates());
        List<Employee> anniversaries = filterAndSort(provider.getData(),
                EMPLOYEE_ANNIVERSARY_COMPARATOR,
                anniversaryPredicates());

        if (skipEmpty && birthdays.isEmpty() && anniversaries.isEmpty()) {
            log.debug("Employee payload creation aborted: empty event lists and skipEmpty flag is true");
            return;
        }

        if (!birthdays.isEmpty() || !skipEmpty) {
            log.debug("Added birthday block to payload: empty = {}, skipEmpty = {}", birthdays.isEmpty(), skipEmpty);
            builder.block(birthday)
                    .block(collectToBulletListString(birthdays, Employee::toBirthdayString));
        }


        if (!anniversaries.isEmpty() || !skipEmpty) {
            log.debug("Added anniversary block to payload: empty = {}, skipEmpty = {}", anniversaries.isEmpty(), skipEmpty);
            builder.block(anniversary)
                    .block(collectToBulletListString(anniversaries, Employee::toWorkAnniversaryString));
        }

        publish(builder.build());
    }
}
