package com.whiskels.notifier.reporting.service.employee.convert;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.infrastructure.slack.builder.SlackPayloadBuilder;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.whiskels.notifier.infrastructure.slack.builder.SlackPayloadBuilder.builder;
import static com.whiskels.notifier.utilities.collections.StreamUtil.collectToBulletListString;

@Slf4j
@RequiredArgsConstructor
public class EmployeeEventReportMessageConverter implements ReportMessageConverter<Employee> {
    private static final Comparator<EmployeeDto> EMPLOYEE_BIRTHDAY_COMPARATOR = Comparator.comparing(EmployeeDto::birthday)
            .thenComparing(EmployeeDto::name);
    private static final Comparator<EmployeeDto> EMPLOYEE_ANNIVERSARY_COMPARATOR = Comparator.comparing(EmployeeDto::appointmentDate)
            .thenComparing(EmployeeDto::name);

    private final List<ReportContext> contexts;
    private final Clock clock;
    private final String noData;
    private final String birthdaySubheader;
    private final String anniversarySubheader;

    @Nonnull
    @Override
    public Iterable<Payload> convert(@Nonnull ReportData<Employee> data) {
        return contexts.stream()
                .map(context -> createReport(data, context))
                .filter(Objects::nonNull)
                .toList();
    }

    private Payload createReport(ReportData<Employee> data, ReportContext context) {
        var skipEmpty = context.getSkipEmpty().test(data.requestDate());

        List<EmployeeDto> birthdays = data.data().stream()
                .filter(employee -> context.getBirthdayPredicate().test(employee, data.requestDate()))
                .map(EmployeeDto::from)
                .sorted(EMPLOYEE_BIRTHDAY_COMPARATOR)
                .collect(Collectors.toList());

        List<EmployeeDto> anniversaries = data.data().stream()
                .filter(employee -> context.getAnniversaryPredicate().test(employee, data.requestDate()))
                .map(EmployeeDto::from)
                .sorted(EMPLOYEE_ANNIVERSARY_COMPARATOR)
                .collect(Collectors.toList());

        if (skipEmpty && birthdays.isEmpty() && anniversaries.isEmpty()) {
            log.warn("Employee payload creation aborted: empty event lists and skipEmpty flag is true: {}", context.getClass().getSimpleName());
            return null;
        }

        SlackPayloadBuilder builder = builder()
                .header(context.getHeaderMapper().apply(data.requestDate()))
                .notifyChannel();

        if (!birthdays.isEmpty() || !skipEmpty) {
            log.debug("Added birthday block to payload: empty = {}, skipEmpty = {}", birthdays.isEmpty(), skipEmpty);
            addBlock(builder, birthdaySubheader, birthdays, EmployeeDto::toBirthdayString);
        }

        if (!anniversaries.isEmpty() || !skipEmpty) {
            log.debug("Added anniversary block to payload: empty = {}, skipEmpty = {}", anniversaries.isEmpty(), skipEmpty);
            addBlock(builder, anniversarySubheader, anniversaries, e -> e.toWorkAnniversaryString(clock));
        }

        return builder.build();
    }

    private void addBlock(SlackPayloadBuilder builder, String header, List<EmployeeDto> data, Function<EmployeeDto, String> mappingFunction) {
        builder.block(header)
                .block(data.isEmpty() ? noData : collectToBulletListString(data, mappingFunction));
    }
}
